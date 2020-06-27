SUMMARY = "Xilinx HDMI Linux Kernel module"
DESCRIPTION = "Out-of-tree HDMI kernel modules provider for MPSoC EG/EV devices"
SECTION = "kernel/modules"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=498a38cdcb922b9e987bbbb46e8a9ee5"

XLNX_HDMI_VERSION = "5.4.0"
PV = "${XLNX_HDMI_VERSION}"

S = "${WORKDIR}/git"

BRANCH ?= "rel-v2020.1"
REPO   ?= "git://github.com/xilinx/hdmi-modules.git;protocol=https"
SRCREV ?= "3a6e440b50263a3ed99492aba3e507d7c130355c"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

inherit module

EXTRA_OEMAKE += "O=${STAGING_KERNEL_BUILDDIR}"
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmp = "zynqmp"
COMPATIBLE_MACHINE_versal = "versal"

PACKAGE_ARCH = "${SOC_FAMILY_ARCH}"
