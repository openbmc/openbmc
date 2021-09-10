#
# Tune Settings for Neoverse-E1
#
DEFAULTTUNE                                        ?= "neoversee1"

TUNEVALID[neoversee1] = "Enable Neoverse-E1 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoversee1', ' -mcpu=neoverse-e1', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "neoversee1"
ARMPKGARCH:tune-neoversee1                          = "neoversee1"
TUNE_FEATURES:tune-neoversee1                       = "${TUNE_FEATURES:tune-armv8-2a-crypto} neoversee1"
PACKAGE_EXTRA_ARCHS:tune-neoversee1                 = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} neoversee1"
BASE_LIB:tune-neoversee1                            = "lib64"
