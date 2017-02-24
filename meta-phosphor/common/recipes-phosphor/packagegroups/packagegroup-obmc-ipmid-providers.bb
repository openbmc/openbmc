SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-libs"

OBMC_IPMID_PROVIDERS = "phosphor-ipmi-fru"

SUMMARY_${PN}-libs = "Extra providers for ipmid."
RDEPENDS_${PN}-libs = "${OBMC_IPMID_PROVIDERS}"

WHITELIST_FORMAT = "{0}-whitelist-native"
OBMC_IPMID_WHITELISTS = "${@compose_list(d, 'WHITELIST_FORMAT', 'OBMC_IPMID_PROVIDERS')}"

DEPENDS_append = "${OBMC_IPMID_WHITELISTS}"
