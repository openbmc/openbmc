FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://minerva-fan.yaml \
"

do_install:append() {
    cat minerva-fan.yaml >> ${D}${settings_datadir}/defaults.yaml
}
