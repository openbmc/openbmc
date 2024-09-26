FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://minerva-fan.yaml \
    file://minerva-temporary-software-versions.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    minerva-temporary-software-versions.yml \
"

do_install:append() {
    cat minerva-fan.yaml >> ${D}${settings_datadir}/defaults.yaml
}
