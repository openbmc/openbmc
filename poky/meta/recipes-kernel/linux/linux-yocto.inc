SUMMARY = "Linux kernel"
SECTION = "kernel"
LICENSE = "GPLv2"
HOMEPAGE = "https://www.yoctoproject.org/"

LIC_FILES_CHKSUM ?= "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.\d+)*)"

RECIPE_NO_UPDATE_REASON = "Recipe is updated through a separate process"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids network access required
# by the use of AUTOREV SRCREVs, which are the default for this recipe.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME") == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != d.getVar("PN"):
        d.delVar("BB_DONT_CACHE")
        raise bb.parse.SkipRecipe("Set PREFERRED_PROVIDER_virtual/kernel to %s to enable it" % (d.getVar("PN")))
}

DEPENDS += "xz-native bc-native"
DEPENDS_append_aarch64 = " libgcc"
KERNEL_CC_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"

DEPENDS_append_nios2 = " libgcc"
KERNEL_CC_append_nios2 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_nios2 = " ${TOOLCHAIN_OPTIONS}"

DEPENDS_append_arc = " libgcc"
KERNEL_CC_append_arc = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_arc = " ${TOOLCHAIN_OPTIONS}"

KERNEL_FEATURES_append_qemuall=" features/debug/printk.scc"

KERNEL_FEATURES_append = " ${@bb.utils.contains('MACHINE_FEATURES', 'numa', 'features/numa/numa.scc', '', d)}"

# A KMACHINE is the mapping of a yocto $MACHINE to what is built
# by the kernel. This is typically the branch that should be built,
# and it can be specific to the machine or shared
# KMACHINE = "UNDEFINED"

LINUX_VERSION_EXTENSION ??= "-yocto-${LINUX_KERNEL_TYPE}"

# Pick up shared functions
inherit kernel
inherit kernel-yocto

B = "${WORKDIR}/linux-${PACKAGE_ARCH}-${LINUX_KERNEL_TYPE}-build"

do_install_append(){
	if [ -n "${KMETA}" ]; then
		rm -rf ${STAGING_KERNEL_DIR}/${KMETA}
	fi
}

# enable kernel-sample for oeqa/runtime/cases's ksample.py test
KERNEL_FEATURES_append_qemuall=" features/kernel-sample/kernel-sample.scc"
