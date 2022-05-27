FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm750 = " file://99-ipmi-kcs.rules.rules"

KCS_DEVICE:evb-npcm750 = "ipmi_kcs1"

do_install:append:evb-npcm750() {
        install -d ${D}/lib/udev/rules.d
        install -m 0644 ${WORKDIR}/99-ipmi-kcs.rules.rules ${D}/lib/udev/rules.d
}
