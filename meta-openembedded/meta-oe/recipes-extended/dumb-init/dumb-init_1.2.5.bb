SUMMARY = "Simple wrapper script which proxies signals to a child"
HOMEPAGE = "https://github.com/Yelp/dumb-init/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5940d39995ea6857d01b8227109c2e9c"

SRCREV = "89c1502b9d40b5cb4a844498b14d74ba1dd559bf"
SRC_URI = "git://github.com/Yelp/dumb-init;branch=master;protocol=https"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS} ${LDFLAGS}'"

do_install() {
    install -d ${D}${base_sbindir}
    install ${S}/dumb-init ${D}${base_sbindir}/
}
