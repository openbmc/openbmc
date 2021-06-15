FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_rpi = " \
	file://lirc-gpio-ir-0.10.patch \
        file://lircd.service \
"
