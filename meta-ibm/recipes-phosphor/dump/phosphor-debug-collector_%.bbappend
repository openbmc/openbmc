FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:ibm-enterprise = " host-dump-transport-pldm"

PACKAGECONFIG:append:ibm-enterprise = " openpower-dumps-extension"

SYSTEMD_SERVICE:${PN}-manager:ibm-enterprise += "clear_hostdumps_poweroff.service"

EXTRA_OEMESON:append:ibm-enterprise = " -DBMC_DUMP_TOTAL_SIZE=409600"
EXTRA_OEMESON:append:ibm-enterprise = " -DBMC_DUMP_MAX_SIZE=20480"

