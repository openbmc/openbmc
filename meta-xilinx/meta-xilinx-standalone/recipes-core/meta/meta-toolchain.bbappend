COMPATIBLE_HOST = "${HOST_SYS}"

# We want a smaller SDK then normal, so don't use nativesdk-packagegroup-sdk-host
# The following should work on both Linux and mingw32

HOST_DEPENDS = " \
   nativesdk-qemu \
   nativesdk-sdk-provides-dummy \
"

TOOLCHAIN_HOST_TASK_xilinx-standalone = "${HOST_DEPENDS} packagegroup-cross-canadian-${MACHINE}"
TOOLCHAIN_TARGET_TASK_xilinx-standalone = "${@multilib_pkg_extend(d, 'packagegroup-newlib-standalone-sdk-target')}"
