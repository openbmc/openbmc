#
# Tune Settings for Neoverse-N2
#
DEFAULTTUNE ?= "neoversen2"

TUNEVALID[neoversen2] = "Enable Neoverse-N2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoversen2', ' -mcpu=neoverse-n2', '', d)}"

# Even though the Neoverse N2 core implemnts the Arm v9.0-A architecture,
# but the support of it in GCC is based on the Arm v8.5-A architecture.
require conf/machine/include/arm/arch-armv8-5a.inc

# Little Endian base configs
AVAILTUNES                                         += "neoversen2 neoversen2-crypto"
ARMPKGARCH:tune-neoversen2                          = "neoversen2"
ARMPKGARCH:tune-neoversen2-crypto                   = "neoversen2-crypto"
TUNE_FEATURES:tune-neoversen2                       = "${TUNE_FEATURES:tune-armv8-5a} neoversen2"
TUNE_FEATURES:tune-neoversen2-crypto                = "${TUNE_FEATURES:tune-neoversen2} crypto"
PACKAGE_EXTRA_ARCHS:tune-neoversen2                 = "${PACKAGE_EXTRA_ARCHS:tune-armv8-5a} neoversen2"
PACKAGE_EXTRA_ARCHS:tune-neoversen2-crypto          = "${PACKAGE_EXTRA_ARCHS:tune-armv8-5a-crypto} neoversen2 neoversen2-crypto"
BASE_LIB:tune-neoversen2                            = "lib64"
BASE_LIB:tune-neoversen2-crypto                     = "lib64"
