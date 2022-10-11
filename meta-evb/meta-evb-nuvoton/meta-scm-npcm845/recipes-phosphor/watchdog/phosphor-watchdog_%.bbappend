FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " \
    file://0001-Customize-phosphor-watchdog-for-Intel-platforms.patch \
    file://0002-Customize-phosphor-watchdog-for-Nuvoton-platform.patch \
    "

# Remove the override to keep service running after DC cycle
SYSTEMD_OVERRIDE:${PN}:remove:scm-npcm845 = "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"
SYSTEMD_SERVICE:${PN}:scm-npcm845 = "phosphor-watchdog.service"
