FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://bios_defs.json"
SRC_URI:append:olympus-nuvoton = " file://0001-main-add-feature-for-updating-BootProgress-and-Opera.patch"

SNOOP_DEVICE = "npcm7xx-lpc-bpc0"

DEPENDS += "nlohmann-json"

do_install:append:olympus-nuvoton() {
        install -d ${D}${sysconfdir}/default/obmc/bios/
        install -m 0644 ${WORKDIR}/bios_defs.json ${D}/${sysconfdir}/default/obmc/bios/
}
