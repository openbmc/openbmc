FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:g220a = " file://g220a.cfg \
			 file://0003-misc-aspeed-Add-Aspeed-UART-routing-control-driver.patch \
			 file://0004-ARM-dts-aspeed-Add-uart-routing-node.patch \
			 file://0005-ARM-dts-aspeed-Enable-g220a-uart-route.patch \
                       "
