FILESEXTRAPATHS_prepend := "${THISDIR}/../../../../recipes-kernel/linux/files:"
SRC_URI_append_gbmc = " file://gbmc.cfg"
SRC_URI_append_gbmc_dev = " file://gbmc-dev.cfg"
SRC_URI_append_gbmc_prod = " file://gbmc-prod.cfg"
