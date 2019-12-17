SUMMARY = "Random number generator daemon"
DESCRIPTION = "Check and feed random data from hardware device to kernel"
AUTHOR = "Philipp Rumpf, Jeff Garzik <jgarzik@pobox.com>, \
          Henrique de Moraes Holschuh <hmh@debian.org>"
HOMEPAGE = "https://github.com/nhorman/rng-tools"
BUGTRACKER = "https://github.com/nhorman/rng-tools/issues"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "sysfsutils"

SRC_URI = "\
    git://github.com/nhorman/rng-tools.git \
    file://init \
    file://default \
    file://rngd.service \
"
SRCREV = "eb5e34c7fb05594760f99d863fbe0401fe297b93"

S = "${WORKDIR}/git"

inherit autotools update-rc.d systemd pkgconfig

PACKAGECONFIG ??= "libgcrypt libjitterentropy"
PACKAGECONFIG_libc-musl = "libargp libjitterentropy"

PACKAGECONFIG[libargp] = "--with-libargp,--without-libargp,argp-standalone,"
PACKAGECONFIG[libgcrypt] = "--with-libgcrypt,--without-libgcrypt,libgcrypt,"
PACKAGECONFIG[libjitterentropy] = "--enable-jitterentropy,--disable-jitterentropy,libjitterentropy"
PACKAGECONFIG[libp11] = "--with-pkcs11,--without-pkcs11,libp11 openssl"
PACKAGECONFIG[nistbeacon] = "--with-nistbeacon,--without-nistbeacon,curl libxml2 openssl"

INITSCRIPT_NAME = "rng-tools"
INITSCRIPT_PARAMS = "start 03 2 3 4 5 . stop 30 0 6 1 ."

SYSTEMD_SERVICE_${PN} = "rngd.service"

# Refer autogen.sh in rng-tools
do_configure_prepend() {
    cp ${S}/README.md ${S}/README
}

do_install_append() {
    install -Dm 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/rng-tools
    install -Dm 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/rng-tools
    install -Dm 0644 ${WORKDIR}/rngd.service \
                     ${D}${systemd_system_unitdir}/rngd.service
    sed -i \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,@SBINDIR@,${sbindir},g' \
        ${D}${sysconfdir}/init.d/rng-tools \
        ${D}${systemd_system_unitdir}/rngd.service
}
