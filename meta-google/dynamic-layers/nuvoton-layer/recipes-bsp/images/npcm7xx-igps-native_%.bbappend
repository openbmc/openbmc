FILESEXTRAPATHS_prepend_gbmc_hoth := "${THISDIR}/${BPN}:"

SRC_URI_append_gbmc_hoth = " file://0001-Set-FIU0_DRD_CFG-and-FIU_Clk_divider-for-gbmc-hoth.patch"
