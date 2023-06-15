SUMMARY = "Linux kernel"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
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
DEPENDS:append:aarch64 = " libgcc"
KERNEL_CC:append:aarch64 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD:append:aarch64 = " ${TOOLCHAIN_OPTIONS}"

DEPENDS:append:nios2 = " libgcc"
KERNEL_CC:append:nios2 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD:append:nios2 = " ${TOOLCHAIN_OPTIONS}"

DEPENDS:append:arc = " libgcc"
KERNEL_CC:append:arc = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD:append:arc = " ${TOOLCHAIN_OPTIONS}"

KERNEL_FEATURES:append:qemuall=" features/debug/printk.scc"

KERNEL_FEATURES:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'numa', 'features/numa/numa.scc', '', d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'vfat', 'cfg/fs/vfat.scc', '', d)}"

# A KMACHINE is the mapping of a yocto $MACHINE to what is built
# by the kernel. This is typically the branch that should be built,
# and it can be specific to the machine or shared
# KMACHINE = "UNDEFINED"

LINUX_VERSION_EXTENSION ??= "-yocto-${LINUX_KERNEL_TYPE}"

# Pick up shared functions
inherit kernel
inherit kernel-yocto

B = "${WORKDIR}/linux-${PACKAGE_ARCH}-${LINUX_KERNEL_TYPE}-build"

do_install:append(){
	if [ -n "${KMETA}" ]; then
		rm -rf ${STAGING_KERNEL_DIR}/${KMETA}
	fi
}

# enable kernel-sample for oeqa/runtime/cases's ksample.py test
KERNEL_FEATURES:append:qemuall=" features/kernel-sample/kernel-sample.scc"

KERNEL_DEBUG_OPTIONS ?= "stack"
KERNEL_EXTRA_ARGS:append:x86-64 = " ${@bb.utils.contains('KERNEL_DEBUG_OPTIONS', 'stack', 'HOST_LIBELF_LIBS="-L${RECIPE_SYSROOT_NATIVE}/usr/lib/pkgconfig/../../../usr/lib/ -lelf"', '', d)}"

do_devshell:prepend() {
    # setup native pkg-config variables (kconfig scripts call pkg-config directly, cannot generically be overriden to pkg-config-native)
    d.setVar("PKG_CONFIG_DIR", "${STAGING_DIR_NATIVE}${libdir_native}/pkgconfig")
    d.setVar("PKG_CONFIG_PATH", "${PKG_CONFIG_DIR}:${STAGING_DATADIR_NATIVE}/pkgconfig")
    d.setVar("PKG_CONFIG_LIBDIR", "${PKG_CONFIG_DIR}")
    d.setVarFlag("PKG_CONFIG_SYSROOT_DIR", "unexport", "1")
    d.appendVar("OE_TERMINAL_EXPORTS", " PKG_CONFIG_DIR PKG_CONFIG_PATH PKG_CONFIG_LIBDIR PKG_CONFIG_SYSROOT_DIR")
}

# CVE exclusion
include recipes-kernel/linux/cve-exclusion.inc
