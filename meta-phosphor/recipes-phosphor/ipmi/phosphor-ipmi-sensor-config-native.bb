SUMMARY = "OpenBMC - IPMI sensors"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native

OBMC_IPMI_SENSORS_PROVIDERS = "phosphor-ipmi-sensor-inventory-mrw-config-native"

DEPENDS_append = " ${OBMC_IPMI_SENSORS_PROVIDERS} "
