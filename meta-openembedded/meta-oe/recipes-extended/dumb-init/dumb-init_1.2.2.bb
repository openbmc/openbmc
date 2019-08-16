SUMMARY = "Simple wrapper script which proxies signals to a child"
HOMEPAGE = "https://github.com/Yelp/dumb-init/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5940d39995ea6857d01b8227109c2e9c"

SRCREV = "b1e978e486114797347deefcc03ab12629a13cc3"
SRC_URI = "git://github.com/Yelp/dumb-init"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS} ${LDFLAGS}'"

do_install() {
    install -d ${D}${base_sbindir}
    install ${S}/dumb-init ${D}${base_sbindir}/
}
