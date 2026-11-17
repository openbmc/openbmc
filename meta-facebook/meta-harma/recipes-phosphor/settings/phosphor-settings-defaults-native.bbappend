FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://harma-temporary-software-versions.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    harma-temporary-software-versions.yml \
"
