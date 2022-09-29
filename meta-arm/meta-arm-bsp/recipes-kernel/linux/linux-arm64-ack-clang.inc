# Clang-specific configuration of kernel build

# We need to add this dependency as the kernel configuration depends on the compiler
do_kernel_configme[depends] += "androidclang-native:do_populate_sysroot"

DEPENDS:append = " androidclang-native"

KERNEL_CC = "${CCACHE}clang ${HOST_CC_KERNEL_ARCH}"
