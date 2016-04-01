DESCRIPTION = "Yocto Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

INC_PR = "r4"

DEPENDS += "xz-native bc-native"
DEPENDS_append_aarch64 = " libgcc"
KERNEL_CC_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"

DEPENDS_append_nios2 = " libgcc"
KERNEL_CC_append_nios2 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_nios2 = " ${TOOLCHAIN_OPTIONS}"

# A KMACHINE is the mapping of a yocto $MACHINE to what is built
# by the kernel. This is typically the branch that should be built,
# and it can be specific to the machine or shared
# KMACHINE = "UNDEFINED"

LINUX_KERNEL_TYPE ?= "standard"

# KMETA ?= ""
KBRANCH ?= "master"
KMACHINE ?= "${MACHINE}"
SRCREV_FORMAT ?= "meta_machine" 

# LEVELS:
#   0: no reporting
#   1: report options that are specified, but not in the final config
#   2: report options that are not hardware related, but set by a BSP
KCONF_AUDIT_LEVEL ?= "1"
KCONF_BSP_AUDIT_LEVEL ?= "0"

LINUX_VERSION_EXTENSION ?= "-yocto-${LINUX_KERNEL_TYPE}"

# Pick up shared functions
inherit kernel
inherit kernel-yocto
require linux-dtb.inc

B = "${WORKDIR}/linux-${PACKAGE_ARCH}-${LINUX_KERNEL_TYPE}-build"

do_install_append(){
	if [ -n "${KMETA}" ]; then
		rm -rf ${STAGING_KERNEL_DIR}/${KMETA}
	fi
}

# extra tasks
addtask kernel_link_vmlinux after do_compile before do_install
addtask validate_branches before do_patch after do_kernel_checkout
addtask kernel_configcheck after do_configure before do_compile
