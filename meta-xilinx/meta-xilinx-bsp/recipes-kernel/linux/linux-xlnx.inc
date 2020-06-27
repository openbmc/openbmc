# This version extension should match CONFIG_LOCALVERSION in defconfig
XILINX_RELEASE_VERSION ?= ""
LINUX_VERSION_EXTENSION ?= "-xilinx-${XILINX_RELEASE_VERSION}"
PV = "${LINUX_VERSION}+git${SRCPV}"

# Sources, by default allow for the use of SRCREV pointing to orphaned tags/commits
KBRANCH ?= "xlnx_rebase_v5.4"
SRCBRANCHARG = "${@['nobranch=1', 'branch=${KBRANCH}'][d.getVar('KBRANCH', True) != '']}"

FILESOVERRIDES_append = ":${XILINX_RELEASE_VERSION}"
KERNELURI ?= "git://github.com/Xilinx/linux-xlnx.git;protocol=https;name=machine"
YOCTO_META ?= "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.4;destsuffix=yocto-kmeta"
SRC_URI = "${KERNELURI};${SRCBRANCHARG} ${YOCTO_META}"

SRCREV_machine ?= "${SRCREV}"
SRCREV_meta ?= "93b1325e4b9bff0d1ce7a2dd85aed0b26e3e76d7"
SRCREV_FORMAT = "machine"

require recipes-kernel/linux/linux-yocto.inc
require linux-microblaze.inc

DESCRIPTION = "Xilinx Kernel"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

EXTKERNELSRC = "${@'1' if d.getVar('EXTERNALSRC') else ''}"

# Force the use of the KBUILD_DEFCONFIG even if some other defconfig was generated in the ${WORKDIR}
do_kernel_metadata_prepend () {
	[ -n "${KBUILD_DEFCONFIG}" ] && [ -e ${WORKDIR}/defconfig ] && rm ${WORKDIR}/defconfig
}

do_configure_prepend () {
	if [ -n "${KBUILD_DEFCONFIG}" ] && [ -n "${EXTKERNELSRC}" ]; then
		cp ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${WORKDIR}/defconfig
	fi
}

inherit kernel-simpleimage

# Default to be only compatible with specific machines or soc families
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE_zynq = ".*"
COMPATIBLE_MACHINE_zynqmp = ".*"
COMPATIBLE_MACHINE_microblaze = ".*"
COMPATIBLE_MACHINE_versal = ".*"

# Use DEFCONFIGs for configuring linux-xlnx kernels
KCONFIG_MODE ?= "alldefconfig"
KBUILD_DEFCONFIG_zynqmp = "xilinx_zynqmp_defconfig"
KBUILD_DEFCONFIG_zynq = "xilinx_zynq_defconfig"
KBUILD_DEFCONFIG_microblaze = "mmu_defconfig"
KBUILD_DEFCONFIG_versal = "xilinx_versal_defconfig"

# Add meta-xilinx kmeta, used for MicroBlaze BSP fragments
FILESEXTRAPATHS_prepend := "${THISDIR}:"
SRC_URI_append = " file://xilinx-kmeta;type=kmeta;name=xilinx-kmeta;destsuffix=xilinx-kmeta"

# MicroBlaze BSP fragments
KERNEL_FEATURES_append_kc705-microblazeel = " bsp/kc705-microblazeel/kc705-microblazeel.scc"

KERNEL_FEATURES_append_zynqmp = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' features/xen/xen.scc', '', d)}"

KERNEL_FEATURES_append_zynqmp = "${@' features/overlay/overlay.scc' if d.getVar('FPGA_MNGR_RECONFIG_ENABLE') == '1' else ''}"

KERNEL_FEATURES_append_versal = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' features/xen/xen.scc', '', d)}"

KERNEL_FEATURES_append_ultra96-zynqmp = " bsp/ultra96-zynqmp/mipi-config-ultra96.scc"

KERNEL_FEATURES_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', ' features/ocicontainer/ocicontainer.scc', '', d)}"
