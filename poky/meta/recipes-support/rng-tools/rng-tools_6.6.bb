SUMMARY = "Random number generator daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/nhorman/rng-tools.git \
           file://0001-If-the-libc-is-lacking-argp-use-libargp.patch \
           file://0002-Add-argument-to-control-the-libargp-dependency.patch \
           file://underquote.patch \
           file://rng-tools-5-fix-textrels-on-PIC-x86.patch \
           file://0001-configure.ac-fix-typo.patch \
           file://init \
           file://default \
           file://rngd.service \
"
SRCREV = "4ebc21d6f387bb7b4b3f6badc429e27b21c0a6ee"
S = "${WORKDIR}/git"

inherit autotools update-rc.d systemd pkgconfig

DEPENDS = " \
           sysfsutils \
          "

PACKAGECONFIG ??= "libgcrypt libjitterentropy"
PACKAGECONFIG_libc-musl = "libargp libjitterentropy"
PACKAGECONFIG[libargp] = "--with-libargp,--without-libargp,argp-standalone,"
PACKAGECONFIG[libgcrypt] = "--with-libgcrypt,--without-libgcrypt,libgcrypt,"
PACKAGECONFIG[libjitterentropy] = "--enable-jitterentropy,--disable-jitterentropy,libjitterentropy"
PACKAGECONFIG[nistbeacon] = "--with-nistbeacon,--without-nistbeacon,curl libxml2 openssl"

# Refer autogen.sh in rng-tools
do_configure_prepend() {
    cp ${S}/README.md ${S}/README
}

do_install_append() {
    # Only install the init script when 'sysvinit' is in DISTRO_FEATURES.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d "${D}${sysconfdir}/init.d"
        install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/rng-tools
        sed -i -e 's,/etc/,${sysconfdir}/,' -e 's,/usr/sbin/,${sbindir}/,' \
            ${D}${sysconfdir}/init.d/rng-tools

        install -d "${D}${sysconfdir}/default"
        install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/rng-tools
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/rngd.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/rngd.service
    fi
}

INITSCRIPT_NAME = "rng-tools"
INITSCRIPT_PARAMS = "start 03 2 3 4 5 . stop 30 0 6 1 ."

SYSTEMD_SERVICE_${PN} = "rngd.service"
