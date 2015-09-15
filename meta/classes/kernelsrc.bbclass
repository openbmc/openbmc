S = "${STAGING_KERNEL_DIR}"
do_fetch[noexec] = "1"
do_unpack[depends] += "virtual/kernel:do_patch"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_package[depends] += "virtual/kernel:do_populate_sysroot"
KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"

inherit linux-kernel-base

