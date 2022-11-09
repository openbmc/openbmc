FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://bletchley-host-acpi-power-state.yaml \
    file://bletchley-frontpanel.yaml \
"
SETTINGS_HOST_TEMPLATES:append = " bletchley-host-acpi-power-state.yaml"
SETTINGS_CHASSIS_TEMPLATES_ZERO_ONLY:append = " bletchley-frontpanel.yaml"

OBMC_CHASSIS_ZERO_ONLY="0"

do_install:append() {
    DEST=${D}${settings_datadir}

    for i in ${OBMC_CHASSIS_ZERO_ONLY};
    do
        for f in ${SETTINGS_CHASSIS_TEMPLATES_ZERO_ONLY};
        do
            sed "s/{}/${i}/g" ${f} >> ${DEST}/defaults.yaml
        done
    done
}
