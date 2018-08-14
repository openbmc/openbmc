SUMMARY = "Debian's start-stop-daemon utility extracted from the dpkg \
package"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://utils/start-stop-daemon.c;endline=21;md5=8fbd0497a7d0b01e99820bffcb58e9ad"
# start-stop-daemon is usually shipped by dpkg
DEPENDS = "ncurses"
RCONFLICTS_${PN} = "dpkg"

SRC_URI = " \
    ${DEBIAN_MIRROR}/main/d/dpkg/dpkg_${PV}.tar.xz \
    file://0001-dpkg-start-stop-daemon-Accept-SIG-prefixed-signal-na.patch \
"

SRC_URI[md5sum] = "e48fcfdb2162e77d72c2a83432d537ca"
SRC_URI[sha256sum] = "07019d38ae98fb107c79dbb3690cfadff877f153b8c4970e3a30d2e59aa66baa"

inherit autotools gettext pkgconfig

S = "${WORKDIR}/dpkg-${PV}"

EXTRA_OECONF = " \
    --without-bz2 \
    --without-selinux \
"

do_install_append () {
    # remove everything that is not related to start-stop-daemon, since there
    # is no explicit rule for only installing ssd
    find ${D} -type f -not -name "*start-stop-daemon*" -exec rm {} \;
    find ${D} -depth -type d -empty -exec rmdir {} \;

    # support for buggy init.d scripts that refer to an alternative
    # explicit path to start-stop-daemon
    if [ "${base_sbindir}" != "${sbindir}" ]; then
        mkdir -p ${D}${base_sbindir}
        ln -sf ${sbindir}/start-stop-daemon ${D}${base_sbindir}/start-stop-daemon
    fi
}
