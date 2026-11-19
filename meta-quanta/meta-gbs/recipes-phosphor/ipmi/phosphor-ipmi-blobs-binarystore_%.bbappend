FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${BPN}:"
SRC_URI:append:gbs = " file://config.json"
FILES:${PN}:append:gbs = " ${datadir}/binaryblob/config.json"

do_install:append:gbs() {
    install -d ${D}${datadir}/binaryblob/
    install ${UNPACKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}
