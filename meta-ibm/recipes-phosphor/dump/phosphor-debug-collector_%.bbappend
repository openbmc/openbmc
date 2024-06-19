FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:p10bmc = " host-dump-transport-pldm"
PACKAGECONFIG:append:witherspoon-tacoma = " host-dump-transport-pldm"

PACKAGECONFIG:append:p10bmc = " openpower-dumps-extension"
PACKAGECONFIG:append:witherspoon-tacoma = " openpower-dumps-extension"

SYSTEMD_SERVICE:${PN}-manager:p10bmc += "clear_hostdumps_poweroff.service"
SYSTEMD_SERVICE:${PN}-manager:witherspoon-tacoma += "clear_hostdumps_poweroff.service"

EXTRA_OEMESON:append:p10bmc = " -DBMC_DUMP_TOTAL_SIZE=409600"
EXTRA_OEMESON:append:p10bmc = " -DBMC_DUMP_MAX_SIZE=20480"

