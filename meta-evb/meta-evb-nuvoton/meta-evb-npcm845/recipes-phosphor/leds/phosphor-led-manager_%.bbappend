FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://LED_GroupManager.conf"

SYSTEMD_OVERRIDE:${PN}-ledmanager += "LED_GroupManager.conf:xyz.openbmc_project.LED.GroupManager.service.d/LED_GroupManager.conf"

# for yaml build issue (openbmc commit 220e194 have already fixed it)
do_compile:prepend() {
    if [ -f "${LED_YAML_PATH}/led.yaml" ]; then
        cp "${LED_YAML_PATH}/led.yaml" "${S}/led.yaml"
    elif [ -f "${STAGING_DATADIR_NATIVE}/${PN}/led.yaml" ]; then
        cp "${STAGING_DATADIR_NATIVE}/${PN}/led.yaml" "${S}/led.yaml"
    elif [ -f "${WORKDIR}/led.yaml" ]; then
        cp "${WORKDIR}/led.yaml" "${S}/led.yaml"
    fi
}
