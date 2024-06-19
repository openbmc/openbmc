DESCRIPTION = "OE helper for manipulating npm cache"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "\
    file://oe-npm-cache \
"

inherit native

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

B = "${WORKDIR}/build"

do_configure() {
    sed -e 's!@@libdir@@!${libdir}!g' < '${UNPACKDIR}/oe-npm-cache' > '${B}/oe-npm-cache'
}

do_install() {
    install -D -p -m 0755 ${B}/oe-npm-cache ${D}${bindir}/oe-npm-cache
}

RDEPENDS:${PN} = "nodejs-native"
