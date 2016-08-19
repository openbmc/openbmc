SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = "packagegroup-obmc-ipmid-providers \
    packagegroup-obmc-ipmid-whitelists-native"

OBMC_IPMID_PROVIDERS="host-ipmid-fru"
OBMC_IPMID_WHITELISTS="\
    ${@append_suffix('${OBMC_IPMID_PROVIDERS}', '-whitelist-native')}"

SUMMARY_packagegroup-obmc-ipmid-providers = "Extra providers for ipmid."
RDEPENDS_packagegroup-obmc-ipmid-providers = "${OBMC_IPMID_PROVIDERS}"

SUMMARY_packagegroup-obmc-ipmid-whitelists-native = "Whitelists for ipmid providers."
DEPENDS_packagegroup-obmc-ipmid-whitelists-native = "${OBMC_IPMID_WHITELISTS}"
