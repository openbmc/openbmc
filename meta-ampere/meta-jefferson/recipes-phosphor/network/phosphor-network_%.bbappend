FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " sync-mac "

SRC_URI:append = " file://config.json "
FILES:${PN} += "${datadir}/network/*.json"

do_install:append() {
    install -d ${D}${datadir}/network/
    install -m 0644 ${UNPACKDIR}/config.json ${D}${datadir}/network/
}
