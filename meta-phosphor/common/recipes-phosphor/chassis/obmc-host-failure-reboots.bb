SUMMARY = "Enables reboots on host failures"
DESCRIPTION = "Manages the settings entry that controls reboots \
"on host failures"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} += "host-failure-reboots.service"
