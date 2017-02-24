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

# Normally the dependency is between do_configure and
# DEPENDS:do_populate_sysroot, but when a package is in the SSTATE cache the
# do_configure step is not re-ran. The do_populate_sysroot tasks can themselves
# be performed in parallel by bitbake.
# Since the do_populate_sysroot task of this recipe doesn't provide the
# whitelists itself, we need an explicit dependency on the whitelist's
# do_populate_sysroot task so that the whitelists are present in time for an
# application that needs them to be do_configured.

WHITELIST_TASK_FORMAT = "{0}:do_populate_sysroot"
do_populate_sysroot[depends] = "${@compose_list(d, 'WHITELIST_TASK_FORMAT', 'OBMC_IPMID_WHITELISTS')}"
