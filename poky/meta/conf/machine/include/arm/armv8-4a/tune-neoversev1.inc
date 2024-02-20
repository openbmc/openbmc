#
# Tune Settings for neoverse-v1
#
DEFAULTTUNE ?= "neoversev1"

TUNEVALID[neoversev1] = "Enable neoverse-v1 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoversev1', ' -mcpu=neoverse-v1', '', d)}"

require conf/machine/include/arm/arch-armv8-4a.inc

AVAILTUNES                                          += "neoversev1"
ARMPKGARCH:tune-neoversev1                           = "neoversev1"
TUNE_FEATURES:tune-neoversev1                        = "aarch64 crypto neoversev1"
PACKAGE_EXTRA_ARCHS:tune-neoversev1                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-4a-crypto} neoversev1"
BASE_LIB:tune-neoversev1                             = "lib64"
