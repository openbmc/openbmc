SUMMARY = "Helper scripts for executing a multi-arch instance of Xilinx QEMU"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
RDEPENDS_${PN} = "qemu-xilinx-native"

inherit native

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "file://qemu-system-aarch64-multiarch"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSROOT_DIRS += "${bindir}/qemu-xilinx"

do_install() {
	install -Dm 0755 ${WORKDIR}/qemu-system-aarch64-multiarch ${D}${bindir}/qemu-system-aarch64-multiarch
}

