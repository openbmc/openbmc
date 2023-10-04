SUMMARY = "Virtual Console lock program"
DESCRIPTION = "Sometimes a malicious local user could cause more problems \
  than a sophisticated remote one. vlock is a program that locks one or more \
  sessions on the Linux console to prevent attackers from gaining physical \
  access to the machine. \
  "
SECTION = "utils"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=a17cb0a873d252440acfdf9b3d0e7fbf"

SRC_URI = "${GENTOO_MIRROR}/${BP}.tar.gz \
       file://disable_vlockrc.patch \
       file://vlock_pam_tally2_reset.patch \
       file://vlock-no_tally.patch \
       file://vlock_pam \
       "

SRC_URI[md5sum] = "378175c7692a8f288e65fd4dbf8a38eb"
SRC_URI[sha256sum] = "85aa5aed1ae49351378a0bd527a013078f0f969372a63164b1944174ae1a5e39"

inherit autotools-brokensep update-alternatives

# authentification method: either pam or shadow
PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', 'shadow', d)}"
PACKAGECONFIG[pam] = "--enable-pam,,libpam,"
PACKAGECONFIG[shadow] = "--enable-shadow,,shadow,"

CFLAGS += "-Wall -W -pedantic -std=gnu99"

do_configure () {
    # The configure tries to use 'getent' to get the group
    # info from the host, which should be avoided.
    sed -i 's/\(ROOT_GROUP=\).*/\1"root"/' ${CONFIGURE_SCRIPT}

    ${CONFIGURE_SCRIPT} \
        VLOCK_GROUP=root \
        ROOT_GROUP=root \
        CC="${CC}" \
        LDFLAGS="${LDFLAGS}" \
        --prefix=${prefix} \
        --libdir=${libdir} \
        --mandir=${mandir} \
        --with-modules="all.so new.so nosysrq.so ttyblank.so vesablank.so" \
        --disable-root-password --enable-debug --disable-fail-count \
        EXTRA_CFLAGS="${CFLAGS}" \
        ${PACKAGECONFIG_CONFARGS}
}

do_install:append () {
    if [ ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', '', d)} = yes ]; then
        install -d -m 0755 ${D}/${sysconfdir}/pam.d
        install -m 0644 ${WORKDIR}/vlock_pam ${D}${sysconfdir}/pam.d/vlock
    fi
}

ALTERNATIVE:${PN} = "vlock"
ALTERNATIVE_PRIORITY = "60"
ALTERNATIVE_LINK_NAME[vlock] = "${bindir}/vlock"
