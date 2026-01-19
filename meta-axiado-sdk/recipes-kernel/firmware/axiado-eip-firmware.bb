SUMMARY = "Axiado EIP IP firmware blobs"
DESCRIPTION = "EIP-207 classification firmware files needed during early boot"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://firmware_eip207_ifpp.bin \
           file://firmware_eip207_ipue.bin \
           file://firmware_eip207_ofpp.bin \
           file://firmware_eip207_opue.bin \
           "

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${UNPACKDIR}/firmware_eip207_ifpp.bin ${D}${nonarch_base_libdir}/firmware/
    install -m 0644 ${UNPACKDIR}/firmware_eip207_ipue.bin ${D}${nonarch_base_libdir}/firmware/
    install -m 0644 ${UNPACKDIR}/firmware_eip207_ofpp.bin ${D}${nonarch_base_libdir}/firmware/
    install -m 0644 ${UNPACKDIR}/firmware_eip207_opue.bin ${D}${nonarch_base_libdir}/firmware/
}

FILES:${PN} = "${nonarch_base_libdir}/firmware"
