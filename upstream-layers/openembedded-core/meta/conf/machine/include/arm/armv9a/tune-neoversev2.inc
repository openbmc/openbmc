#
# Tune Settings for neoverse-v2
#
DEFAULTTUNE ?= "neoversev2"

TUNEVALID[neoversev2] = "Enable neoverse-v2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoversev2', ' -mcpu=neoverse-v2', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                          += "neoversev2"
ARMPKGARCH:tune-neoversev2                           = "neoversev2"
TUNE_FEATURES:tune-neoversev2                        = "aarch64 crypto neoversev2"
PACKAGE_EXTRA_ARCHS:tune-neoversev2                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} neoversev2"
BASE_LIB:tune-neoversev2                             = "lib64"
