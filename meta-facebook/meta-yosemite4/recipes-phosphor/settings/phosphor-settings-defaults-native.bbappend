FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://yosemite4-temporary-software-versions.yml \
    file://yosemite4-temporary-host-software-versions.yml \
    file://yosemite4-disable-auto-reboot.override.yml \
"

SETTINGS_HOST_TEMPLATES:append = " \
    yosemite4-temporary-host-software-versions.yml \
    yosemite4-disable-auto-reboot.override.yml \
"

SETTINGS_BMC_TEMPLATES:append = " \
    yosemite4-temporary-software-versions.yml \
"
