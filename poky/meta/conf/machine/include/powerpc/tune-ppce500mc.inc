DEFAULTTUNE ?= "ppce500mc"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppce500mc] = "Enable ppce500mc specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce500mc', ' -mcpu=e500mc', '', d)}"

AVAILTUNES += "ppce500mc"
TUNE_FEATURES:tune-ppce500mc = "m32 fpu-hard ppce500mc bigendian"
TUNE_PKGARCH:tune-ppce500mc = "ppce500mc"
PACKAGE_EXTRA_ARCHS:tune-ppce500mc = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppce500mc"

# pass -mcpu=e500mc for ppce500mc kernel cross compile
TARGET_CC_KERNEL_ARCH = "-mcpu=e500mc"
