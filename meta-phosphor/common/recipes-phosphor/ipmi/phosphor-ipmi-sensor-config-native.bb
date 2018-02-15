SUMMARY = "OpenBMC - IPMI sensors"
PR = "r1"

inherit native
inherit obmc-phosphor-license

OBMC_IPMI_SENSORS_PROVIDERS = "phosphor-ipmi-sensor-inventory-mrw-config-native"

DEPENDS_append = " ${OBMC_IPMI_SENSORS_PROVIDERS} "
