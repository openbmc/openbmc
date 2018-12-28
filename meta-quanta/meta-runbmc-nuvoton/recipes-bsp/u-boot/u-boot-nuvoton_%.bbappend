FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

UBRANCH = "runbmc_mdio"
SRC_URI = "git://github.com/Nuvoton-Israel/u-boot.git;branch=${UBRANCH}"
SRCREV = "${AUTOREV}"
