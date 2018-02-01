S = "${STAGING_KERNEL_DIR}"
deltask do_fetch
deltask do_unpack
do_patch[depends] += "virtual/kernel:do_patch"
do_patch[noexec] = "1"
do_package[depends] += "virtual/kernel:do_populate_sysroot"
KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"

inherit linux-kernel-base

