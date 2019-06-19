DEFAULTTUNE ?= "thunderx"
AVAILTUNES += "thunderx thunderx_be"

TUNEVALID[thunderx] = "Enable instructions for Cavium ThunderX"

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'thunderx', ' -mcpu=thunderx', '',d)}"

require conf/machine/include/arm/arch-armv8a.inc

ARMPKGARCH_tune-thunderx ?= "thunderx"
ARMPKGARCH_tune-thunderx_be ?= "thunderx_be"

TUNE_FEATURES_tune-thunderx = "${TUNE_FEATURES_tune-aarch64} thunderx"
TUNE_FEATURES_tune-thunderx_be = "${TUNE_FEATURES_tune-thunderx} bigendian"
BASE_LIB_tune-thunderx = "lib64"
BASE_LIB_tune-thunderx_be = "lib64"

PACKAGE_EXTRA_ARCHS_tune-thunderx = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} thunderx"
PACKAGE_EXTRA_ARCHS_tune-thunderx_be = "aarch64_be thunderx_be"
