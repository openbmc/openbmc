FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://spdm-responder-emu"

do_install:append() {
   install -d ${D}${sysconfdir}/default
   install -m 0644 ${UNPACKDIR}/spdm-responder-emu ${D}${sysconfdir}/default/spdm-responder-emu
}
