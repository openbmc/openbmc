FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"

SRC_URI:append:mori = " file://config.json"

do_install:append:mori() {
    install -d ${D}${datadir}/binaryblob/
    install ${UNPACKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}

FILES:${PN}:append:mori = " ${datadir}/binaryblob/config.json"
