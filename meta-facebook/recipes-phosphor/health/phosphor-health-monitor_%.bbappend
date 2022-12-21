FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://bmc_health_config.json \
                 "

CPU_CRIT_VAL ?= "90.0"
CPU_CRIT_TGT ?= "reboot.target"
CPU_WARN_VAL ?= "80.0"
CPU_WARN_TGT ?= ""

MEM_CRIT_VAL ?= "85.0"
MEM_CRIT_TGT ?= "reboot.target"
MEM_WARN_VAL ?= "70.0"
MEM_WARN_TGT ?= ""

STORAGE_CRIT_VAL ?= "95.0"
STORAGE_CRIT_TGT ?= ""
STORAGE_WARN_VAL ?= "90.0"
STORAGE_WARN_TGT ?= ""

do_install:prepend() {
    sed -i "s/\"CPU_CRIT_VAL\"/${CPU_CRIT_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/CPU_CRIT_TGT/${CPU_CRIT_TGT}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/\"CPU_WARN_VAL\"/${CPU_WARN_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/CPU_WARN_TGT/${CPU_WARN_TGT}/g" ${WORKDIR}/bmc_health_config.json

    sed -i "s/\"MEM_CRIT_VAL\"/${MEM_CRIT_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/MEM_CRIT_TGT/${MEM_CRIT_TGT}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/\"MEM_WARN_VAL\"/${MEM_WARN_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/MEM_WARN_TGT/${MEM_WARN_TGT}/g" ${WORKDIR}/bmc_health_config.json

    sed -i "s/\"STORAGE_CRIT_VAL\"/${STORAGE_CRIT_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/STORAGE_CRIT_TGT/${STORAGE_CRIT_TGT}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/\"STORAGE_WARN_VAL\"/${STORAGE_WARN_VAL}/g" ${WORKDIR}/bmc_health_config.json
    sed -i "s/STORAGE_WARN_TGT/${STORAGE_WARN_TGT}/g" ${WORKDIR}/bmc_health_config.json
}