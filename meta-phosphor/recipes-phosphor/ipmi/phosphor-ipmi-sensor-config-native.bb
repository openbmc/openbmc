SUMMARY = "OpenBMC - IPMI sensors"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " ${OBMC_IPMI_SENSORS_PROVIDERS} "
PR = "r1"

inherit native

OBMC_IPMI_SENSORS_PROVIDERS = "phosphor-ipmi-sensor-inventory-mrw-config-native"
