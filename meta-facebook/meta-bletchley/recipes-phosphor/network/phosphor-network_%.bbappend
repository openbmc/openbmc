FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " sync-mac"

EXTRA_OEMESON = "-Dforce-sync-mac=true"

SRC_URI += " \
    file://config.json \
    "
FILES:${PN} += "${datadir}/network/*.json"

do_install:append() {
    install -d ${D}${datadir}/network/
    install -m 0644 ${WORKDIR}/config.json ${D}${datadir}/network/
}
