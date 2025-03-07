FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://ventura-temporary-software-versions.yaml \
"

do_install:append() {
    cat ventura-temporary-software-versions.yaml >> ${D}${settings_datadir}/defaults.yaml
}
