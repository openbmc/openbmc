SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-libs"

OBMC_IPMID_PROVIDERS = "phosphor-ipmi-fru"

SUMMARY_${PN}-libs = "Extra providers for ipmid."
RDEPENDS_${PN}-libs = "${OBMC_IPMID_PROVIDERS}"

OBMC_IPMID_WHITELISTS="\
    ${@append_suffix('${OBMC_IPMID_PROVIDERS}', '-whitelist-native')}"

DEPENDS_append = "${OBMC_IPMID_WHITELISTS}"