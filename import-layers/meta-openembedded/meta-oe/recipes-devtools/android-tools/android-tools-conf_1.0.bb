DESCRIPTION = "Different utilities from Android - corressponding configuration files"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://android-gadget-setup"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/android-gadget-setup ${D}${bindir}
}
