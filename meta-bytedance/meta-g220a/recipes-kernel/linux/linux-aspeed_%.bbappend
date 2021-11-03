FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:g220a = " file://g220a.cfg \
			 file://0005-ARM-dts-aspeed-Enable-g220a-uart-route.patch \
                       "
