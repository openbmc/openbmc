FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:rpi = " \
	file://lirc-gpio-ir-0.10.patch \
        file://lircd.service \
"
