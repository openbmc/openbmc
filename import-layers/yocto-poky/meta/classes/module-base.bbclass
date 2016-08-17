inherit kernel-arch

# This is instead of DEPENDS = "virtual/kernel"
do_configure[depends] += "virtual/kernel:do_compile_kernelmodules"

export OS = "${TARGET_OS}"
export CROSS_COMPILE = "${TARGET_PREFIX}"

# This points to the build artefacts from the main kernel build
# such as .config and System.map
# Confusingly it is not the module build output (which is ${B}) but
# we didn't pick the name.
export KBUILD_OUTPUT = "${STAGING_KERNEL_BUILDDIR}"

export KERNEL_VERSION = "${@base_read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"
KERNEL_OBJECT_SUFFIX = ".ko"

# kernel modules are generally machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Function to ensure the kernel scripts are created. Expected to
# be called before do_compile. See module.bbclass for an example.
do_make_scripts() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS 
	make CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
	           -C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} scripts
}
