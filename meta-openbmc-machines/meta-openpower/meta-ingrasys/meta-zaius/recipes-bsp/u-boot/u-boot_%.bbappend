FILESEXTRAPATHS_prepend_zaius := "${THISDIR}/${PN}:"
SRC_URI_append_zaius = " file://0001-board-aspeed-Add-reset_phy-for-Zaius.patch \
                         file://0002-board-aspeed-aspeednic-Use-MAC2-for-networking.patch"
