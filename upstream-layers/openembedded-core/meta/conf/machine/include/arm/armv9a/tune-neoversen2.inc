#
# Tune Settings for Neoverse-N2
#
DEFAULTTUNE ?= "neoversen2"

TUNEVALID[neoversen2] = "Enable Neoverse-N2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoversen2', ' -mcpu=neoverse-n2', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

# Little Endian base configs
AVAILTUNES                                         += "neoversen2 neoversen2-crypto"
ARMPKGARCH:tune-neoversen2                          = "neoversen2"
ARMPKGARCH:tune-neoversen2-crypto                   = "neoversen2-crypto"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-neoversen2                       = "aarch64 neoversen2"
TUNE_FEATURES:tune-neoversen2-crypto                = "${TUNE_FEATURES:tune-neoversen2} crypto"
PACKAGE_EXTRA_ARCHS:tune-neoversen2                 = "${PACKAGE_EXTRA_ARCHS:tune-armv9a} neoversen2"
PACKAGE_EXTRA_ARCHS:tune-neoversen2-crypto          = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} neoversen2 neoversen2-crypto"
BASE_LIB:tune-neoversen2                            = "lib64"
BASE_LIB:tune-neoversen2-crypto                     = "lib64"
