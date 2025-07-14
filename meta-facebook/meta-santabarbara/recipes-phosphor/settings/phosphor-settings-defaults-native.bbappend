FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://santabarbara-temporary-software-versions.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    santabarbara-temporary-software-versions.yml \
"
