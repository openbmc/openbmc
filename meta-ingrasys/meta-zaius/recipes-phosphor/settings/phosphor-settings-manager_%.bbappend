FILESEXTRAPATHS:append:zaius := ":${THISDIR}/${PN}"
SRC_URI:append:zaius = " file://powerpolicy-default-ALWAYS_POWER_ON.override.yml"
SRC_URI:append:zaius = " file://timeowner-host-no-ntp.override.yml"
