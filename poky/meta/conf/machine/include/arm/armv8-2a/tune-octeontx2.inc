DEFAULTTUNE ?= "octeontx2"

TUNEVALID[octeontx2] = "Enable Marvell octeontx2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'octeontx2', ' -mcpu=octeontx2', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES += "octeontx2"
ARMPKGARCH:tune-octeontx2             = "octeontx2"
TUNE_FEATURES:tune-octeontx2          = "${TUNE_FEATURES:tune-armv8-2a-crypto} octeontx2"
PACKAGE_EXTRA_ARCHS:tune-octeontx2    = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} octeontx2"
BASE_LIB:tune-octeontx2               = "lib64"
