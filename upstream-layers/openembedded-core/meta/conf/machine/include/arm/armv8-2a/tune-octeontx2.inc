DEFAULTTUNE ?= "octeontx2"

TUNEVALID[octeontx2] = "Enable Marvell octeontx2 specific processor optimizations"
CPU_TUNE_OCTEONTX2 ?= "-mcpu=octeontx2"
CPU_TUNE_OCTEONTX2:toolchain-clang = "-march=armv8.2-a"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'octeontx2', ' ${CPU_TUNE_OCTEONTX2}', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES += "octeontx2"
ARMPKGARCH:tune-octeontx2             = "octeontx2"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-octeontx2          = "aarch64 crypto octeontx2"
PACKAGE_EXTRA_ARCHS:tune-octeontx2    = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} octeontx2"
BASE_LIB:tune-octeontx2               = "lib64"
