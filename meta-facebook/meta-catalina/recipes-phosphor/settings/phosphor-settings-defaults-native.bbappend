FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://catalina-temporary-software-versions.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    catalina-temporary-software-versions.yml \
"
