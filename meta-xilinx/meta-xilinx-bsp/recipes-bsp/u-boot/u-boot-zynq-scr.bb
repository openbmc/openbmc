SUMMARY = "U-boot boot scripts for Xilinx devices"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "u-boot-mkimage-native"

inherit deploy nopackages

INHIBIT_DEFAULT_DEPS = "1"

COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE_zynqmp = "zynqmp"
COMPATIBLE_MACHINE_zynq = "zynq"
COMPATIBLE_MACHINE_versal = "versal"

KERNELDT = "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split(' ')[0]) if d.getVar('KERNEL_DEVICETREE') else ''}"
DEVICE_TREE_NAME ?= "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/dtb', 'device-tree', 'system.dtb', d.getVar('KERNELDT'), d)}"
#Need to copy a rootfs.cpio.gz.u-boot as uramdisk.image.gz into boot partition
RAMDISK_IMAGE ?= ""
RAMDISK_IMAGE_zynq ?= "uramdisk.image.gz"

KERNEL_BOOTCMD_zynqmp ?= "booti"
KERNEL_BOOTCMD_zynq ?= "bootm"
KERNEL_BOOTCMD_versal ?= "booti"

BOOTMODE ?= "sd"
BOOTMODE_versal ?= "qspi"

SRC_URI = " \
            file://boot.cmd.sd.zynq \
            file://boot.cmd.sd.zynqmp \
            file://boot.cmd.qspi.versal \
            file://pxeboot.pxe \
            "
PACKAGE_ARCH = "${MACHINE_ARCH}"

UBOOTSCR_BASE_NAME ?= "${PN}-${PKGE}-${PKGV}-${PKGR}-${DATETIME}"
UBOOTSCR_BASE_NAME[vardepsexclude] = "DATETIME"
UBOOTPXE_CONFIG ?= "pxelinux.cfg"
UBOOTPXE_CONFIG_NAME = "${UBOOTPXE_CONFIG}-${DATETIME}"
UBOOTPXE_CONFIG_NAME[vardepsexclude] = "DATETIME"

DEVICETREE_ADDRESS_zynqmp ?= "0x4000000"
DEVICETREE_ADDRESS_zynq ?= "0x2000000"
DEVICETREE_ADDRESS_versal ?= "0x1000"
KERNEL_LOAD_ADDRESS_zynqmp ?= "0x80000"
KERNEL_LOAD_ADDRESS_zynq ?= "0x2080000"
KERNEL_LOAD_ADDRESS_versal ?= "0x80000"

RAMDISK_IMAGE_ADDRESS_zynq ?= "0x4000000"
RAMDISK_IMAGE_ADDRESS_versal ?= "0x6000000"

do_configure[noexec] = "1"
do_install[noexec] = "1"

do_compile() {
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_LOAD_ADDRESS@@/${KERNEL_LOAD_ADDRESS}/' \
        -e 's/@@DEVICE_TREE_NAME@@/${DEVICE_TREE_NAME}/' \
        -e 's/@@DEVICETREE_ADDRESS@@/${DEVICETREE_ADDRESS}/' \
        -e 's/@@RAMDISK_IMAGE@@/${RAMDISK_IMAGE}/' \
        -e 's/@@RAMDISK_IMAGE_ADDRESS@@/${RAMDISK_IMAGE_ADDRESS}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        "${WORKDIR}/boot.cmd.${BOOTMODE}.${SOC_FAMILY}" > "${WORKDIR}/boot.cmd"
    mkimage -A arm -T script -C none -n "Boot script" -d "${WORKDIR}/boot.cmd" boot.scr
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@DEVICE_TREE_NAME@@/${DEVICE_TREE_NAME}/' \
	"${WORKDIR}/pxeboot.pxe" > "pxeboot.pxe"
}


do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 boot.scr ${DEPLOYDIR}/${UBOOTSCR_BASE_NAME}.scr
    ln -sf ${UBOOTSCR_BASE_NAME}.scr ${DEPLOYDIR}/boot.scr
    install -d ${DEPLOYDIR}/pxeboot/${UBOOTPXE_CONFIG_NAME}
    install -m 0644 pxeboot.pxe ${DEPLOYDIR}/pxeboot/${UBOOTPXE_CONFIG_NAME}/default
    ln -sf pxeboot/${UBOOTPXE_CONFIG_NAME} ${DEPLOYDIR}/${UBOOTPXE_CONFIG}
}

addtask do_deploy after do_compile before do_build
