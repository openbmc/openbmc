DEFAULTTUNE ?= "thunderx"
AVAILTUNES += "thunderx thunderx_be"

TUNEVALID[thunderx] = "Enable instructions for Cavium ThunderX"

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'thunderx', ' -mcpu=thunderx', '',d)}"

require conf/machine/include/arm/arch-armv8a.inc

ARMPKGARCH:tune-thunderx ?= "thunderx"
ARMPKGARCH:tune-thunderx_be ?= "thunderx"

TUNE_FEATURES:tune-thunderx = "${TUNE_FEATURES:tune-aarch64} thunderx"
TUNE_FEATURES:tune-thunderx_be = "${TUNE_FEATURES:tune-thunderx} bigendian"
BASE_LIB:tune-thunderx = "lib64"
BASE_LIB:tune-thunderx_be = "lib64"

PACKAGE_EXTRA_ARCHS:tune-thunderx = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} thunderx"
PACKAGE_EXTRA_ARCHS:tune-thunderx_be = "aarch64_be thunderx_be"
