FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# utilize a 600s default timer config for ibm-enterprise
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:ibm-enterprise = " obmc/watchdog/poweron"
