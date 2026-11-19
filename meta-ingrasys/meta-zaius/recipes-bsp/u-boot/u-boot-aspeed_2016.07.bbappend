FILESEXTRAPATHS:prepend:zaius := "${THISDIR}/${BPN}:"
SRC_URI:append:zaius = " file://0001-board-aspeed-Add-reset_phy-for-Zaius.patch"
