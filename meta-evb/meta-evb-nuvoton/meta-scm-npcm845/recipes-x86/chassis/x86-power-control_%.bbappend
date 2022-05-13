FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://power-config-host0.json"
SRC_URI:append:scm-npcm845 = " file://0001-skip-POWER_BUTTON-and-POST_COMPLETE.patch"

FILES:${PN} += " ${datadir}/x86-power-control/power-config-host0.json \"

do_install:append:scm-npcm845() {
    install -d ${D}${datadir}/x86-power-control
    install -m 0644 -D ${WORKDIR}/power-config-host0.json \
        ${D}${datadir}/x86-power-control/power-config-host0.json
}
