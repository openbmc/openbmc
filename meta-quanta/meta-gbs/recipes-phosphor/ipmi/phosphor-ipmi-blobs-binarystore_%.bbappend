FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://config.json"
FILES:${PN}:append:gbs = " ${datadir}/binaryblob/config.json"

do_install:append:gbs() {
    install -d ${D}${datadir}/binaryblob/
    install ${WORKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}
