FILESEXTRAPATHS_prepend_lanyang := "${THISDIR}/${PN}:"
SRC_URI_append_lanyang = " file://0001-board-aspeed-Add-reset_phy-for-Lanyang.patch \
                         file://0002-board-aspeed-aspeednic-Use-MAC2-for-networking.patch"
