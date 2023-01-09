FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "ef2b28ee4699abaa2770bb333cd889b7f71e2e45"

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
