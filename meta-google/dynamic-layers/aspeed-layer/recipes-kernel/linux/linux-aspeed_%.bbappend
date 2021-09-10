FILESEXTRAPATHS:prepend := "${THISDIR}/../../../../recipes-kernel/linux/files:"
SRC_URI:append:gbmc = " file://gbmc.cfg"
SRC_URI:append:gbmc:dev = " file://gbmc-dev.cfg"
SRC_URI:append:gbmc:prod = " file://gbmc-prod.cfg"
