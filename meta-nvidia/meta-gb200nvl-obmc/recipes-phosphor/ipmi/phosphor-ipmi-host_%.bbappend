FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append = " arm-sbmr"
PACKAGECONFIG:append = " dynamic-sensors"
PACKAGECONFIG:remove = "softoff"

OBMC_ORG_IPMI_OEM_PROVIDERS = "nvidia"