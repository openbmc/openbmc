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
    file://0001-rngd_jitter-fix-O_NONBLOCK-setting-for-entropy-pipe.patch \
    file://0002-rngd_jitter-initialize-AES-key-before-setting-the-en.patch \
    file://0003-rngd_jitter-always-read-from-entropy-pipe-before-set.patch \
    file://init \
    file://default \
    file://rngd.service \
"
SRCREV = "4a865797a69dd38c64a86aa32884ecc9ba7b4d08"

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
