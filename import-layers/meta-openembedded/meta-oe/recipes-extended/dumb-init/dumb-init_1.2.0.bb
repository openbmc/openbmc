SUMMARY = "Simple wrapper script which proxies signals to a child"
HOMEPAGE = "https://github.com/Yelp/dumb-init/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5940d39995ea6857d01b8227109c2e9c"

SRC_URI = "https://github.com/Yelp/dumb-init/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "4eb7f43d7823686723ff7ac1bad097cb"
SRC_URI[sha256sum] = "74486997321bd939cad2ee6af030f481d39751bc9aa0ece84ed55f864e309a3f"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS} ${LDFLAGS}'"

do_install() {
    install -d ${D}${base_sbindir}
    install ${S}/dumb-init ${D}${base_sbindir}/
}
