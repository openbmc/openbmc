SUMMARY = "Linux kernel module for Video Code Unit"
DESCRIPTION = "Out-of-tree VCU decoder, encoder and common kernel modules provider for MPSoC EV devices"
SECTION = "kernel/modules"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=eb723b61539feef013de476e68b5c50a"

XILINX_VCU_VERSION = "1.0.0"
XILINX_RELEASE_VERSION = "v2019.1"
PV = "${XILINX_VCU_VERSION}-xilinx-${XILINX_RELEASE_VERSION}+git${SRCPV}"

S = "${WORKDIR}/git"

BRANCH ?= "master-rel-2019.1"
REPO ?= "git://github.com/xilinx/vcu-modules.git;protocol=https"
SRCREV ?= "13a8e5b3f614d94081481a808aa8d4bd00b26d76"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

inherit module

EXTRA_OEMAKE += "O=${STAGING_KERNEL_BUILDDIR}"

RDEPENDS_${PN} = "vcu-firmware"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmp = "zynqmp"

PACKAGE_ARCH = "${SOC_FAMILY}"
