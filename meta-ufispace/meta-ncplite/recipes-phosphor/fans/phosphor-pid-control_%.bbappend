FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://config.json"

FILES:${PN}:append = " ${datadir}/swampd/config.json"

do_install:append:ncplite() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${UNPACKDIR}/config.json ${D}${datadir}/swampd/config.json
}
