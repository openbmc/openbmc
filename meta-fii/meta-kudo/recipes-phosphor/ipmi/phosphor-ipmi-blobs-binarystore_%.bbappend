FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${BPN}:"
SRC_URI:append:kudo = " file://config.json"
FILES:${PN}:append:kudo = " ${datadir}/binaryblob/config.json"

do_install:append:kudo() {
    install -d ${D}${datadir}/binaryblob/
    install ${UNPACKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}
