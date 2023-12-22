FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://yosemite4-bios-version.yml \
"
SETTINGS_HOST_TEMPLATES:append = " yosemite4-bios-version.yml"
