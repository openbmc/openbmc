FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit systemd

SRC_URI:append = " file://bmc_health_config.json \
                  file://clean-up-filesystem \
                  file://clean-up-filesystem.service \
                 "

CPU_CRIT_VAL ?= "90.0"
CPU_CRIT_TGT ?= ""
CPU_WARN_VAL ?= "80.0"
CPU_WARN_TGT ?= ""

MEM_CRIT_VAL ?= "10.0"
MEM_CRIT_TGT ?= "reboot.target"
MEM_WARN_VAL ?= "15.0"
MEM_WARN_TGT ?= ""

STORAGE_CRIT_VAL ?= "5.0"
STORAGE_CRIT_TGT ?= "clean-up-filesystem.service"
STORAGE_WARN_VAL ?= "10.0"
STORAGE_WARN_TGT ?= ""

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    clean-up-filesystem.service \
    "

do_install:prepend() {
    sed -i "s/\"CPU_CRIT_VAL\"/${CPU_CRIT_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/CPU_CRIT_TGT/${CPU_CRIT_TGT}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/\"CPU_WARN_VAL\"/${CPU_WARN_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/CPU_WARN_TGT/${CPU_WARN_TGT}/g" ${UNPACKDIR}/bmc_health_config.json

    sed -i "s/\"MEM_CRIT_VAL\"/${MEM_CRIT_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/MEM_CRIT_TGT/${MEM_CRIT_TGT}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/\"MEM_WARN_VAL\"/${MEM_WARN_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/MEM_WARN_TGT/${MEM_WARN_TGT}/g" ${UNPACKDIR}/bmc_health_config.json

    sed -i "s/\"STORAGE_CRIT_VAL\"/${STORAGE_CRIT_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/STORAGE_CRIT_TGT/${STORAGE_CRIT_TGT}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/\"STORAGE_WARN_VAL\"/${STORAGE_WARN_VAL}/g" ${UNPACKDIR}/bmc_health_config.json
    sed -i "s/STORAGE_WARN_TGT/${STORAGE_WARN_TGT}/g" ${UNPACKDIR}/bmc_health_config.json
}

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/clean-up-filesystem.service ${D}${systemd_system_unitdir}/clean-up-filesystem.service
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/clean-up-filesystem ${D}${libexecdir}/${PN}/
}
