FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://0001-olympus-kcsbridged.patch"
SRC_URI:append:olympus-nuvoton = " file://99-ipmi-kcs.rules.rules"

KCS_DEVICE:olympus-nuvoton = "ipmi_kcs1"

do_install:append:olympus-nuvoton() {
        install -d ${D}/lib/udev/rules.d
        install -m 0644 ${WORKDIR}/99-ipmi-kcs.rules.rules ${D}/lib/udev/rules.d
}
