FILESEXTRAPATHS:prepend:gbmc:hoth := "${THISDIR}/${BPN}:"

SRC_URI:append:gbmc:hoth = " file://0001-Set-FIU0_DRD_CFG-and-FIU_Clk_divider-for-gbmc-hoth.patch"
