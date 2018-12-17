# This version extension should match CONFIG_LOCALVERSION in defconfig
XILINX_RELEASE_VERSION ?= ""
LINUX_VERSION_EXTENSION ?= "-xilinx-${XILINX_RELEASE_VERSION}"
PV = "${LINUX_VERSION}${LINUX_VERSION_EXTENSION}+git${SRCPV}"

# Sources, by default allow for the use of SRCREV pointing to orphaned tags/commits
KBRANCH ?= ""
SRCBRANCHARG = "${@['nobranch=1', 'branch=${KBRANCH}'][d.getVar('KBRANCH', True) != '']}"

FILESOVERRIDES_append = ":${XILINX_RELEASE_VERSION}"
KERNELURI ?= "git://github.com/Xilinx/linux-xlnx.git;protocol=https"
SRC_URI = "${KERNELURI};${SRCBRANCHARG}"

SRCREV_machine ?= "${SRCREV}"

require recipes-kernel/linux/linux-yocto.inc
require linux-microblaze.inc

DESCRIPTION = "Xilinx Kernel"

# Force the use of the KBUILD_DEFCONFIG even if some other defconfig was generated in the ${WORKDIR}
do_kernel_metadata_prepend () {
	[ -n "${KBUILD_DEFCONFIG}" ] && [ -e ${WORKDIR}/defconfig ] && rm ${WORKDIR}/defconfig
}

inherit kernel-simpleimage

# Default to be only compatible with specific machines or soc families
COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE_zynq = ".*"
COMPATIBLE_MACHINE_zynqmp = ".*"
COMPATIBLE_MACHINE_microblaze = ".*"

# Use DEFCONFIGs for configuring linux-xlnx kernels
KCONFIG_MODE ?= "alldefconfig"
KBUILD_DEFCONFIG_zynqmp = "xilinx_zynqmp_defconfig"
KBUILD_DEFCONFIG_zynq = "xilinx_zynq_defconfig"
KBUILD_DEFCONFIG_microblaze = "mmu_defconfig"

# Add meta-xilinx kmeta, used for MicroBlaze BSP fragments
FILESEXTRAPATHS_prepend := "${THISDIR}:"
SRC_URI_append = " file://xilinx-kmeta;type=kmeta;name=xilinx-kmeta;destsuffix=xilinx-kmeta"

# MicroBlaze BSP fragments
KERNEL_FEATURES_append_kc705-microblazeel = " bsp/kc705-microblazeel/kc705-microblazeel.scc"

KERNEL_FEATURES_append_zynqmp = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' bsp/xilinx/xen.scc', '', d)}"

KERNEL_FEATURES_append_zynqmp = "${@bb.utils.contains('IMAGE_FEATURES', 'fpga-manager', ' features/overlay/overlay.scc', '', d)}"

