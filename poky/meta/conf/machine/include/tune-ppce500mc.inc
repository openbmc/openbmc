DEFAULTTUNE ?= "ppce500mc"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppce500mc] = "Enable ppce500mc specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce500mc', ' -mcpu=e500mc', '', d)}"

AVAILTUNES += "ppce500mc"
TUNE_FEATURES_tune-ppce500mc = "m32 fpu-hard ppce500mc"
TUNE_PKGARCH_tune-ppce500mc = "ppce500mc"
PACKAGE_EXTRA_ARCHS_tune-ppce500mc = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppce500mc"

# glibc configure options to get e500mc specific library (for sqrt)
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'ppce500mc', '-with-cpu=e500mc', '', d)}"

# pass -mcpu=e500mc for ppce500mc kernel cross compile
TARGET_CC_KERNEL_ARCH = "-mcpu=e500mc"
