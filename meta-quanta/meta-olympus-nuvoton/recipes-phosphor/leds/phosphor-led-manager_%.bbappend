FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://LED_GroupManager.conf"

SYSTEMD_OVERRIDE:${PN}:olympus-nuvoton += " \
    LED_GroupManager.conf:xyz.openbmc_project.LED.GroupManager.service.d/LED_GroupManager.conf"

