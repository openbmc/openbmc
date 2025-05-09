FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:${THISDIR}/${PN}/${MACHINE}:"

SRC_URI:append = " \
    file://platform-temporary-software-versions.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    platform-temporary-software-versions.yml \
"
