FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://yosemite4-bios-version.yml \
                   file://yosemite4-disable-auto-reboot.override.yml \
"

SETTINGS_HOST_TEMPLATES:append = " yosemite4-bios-version.yml yosemite4-disable-auto-reboot.override.yml "
