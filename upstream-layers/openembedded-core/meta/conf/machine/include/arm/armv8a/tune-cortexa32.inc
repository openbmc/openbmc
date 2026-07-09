DEFAULTTUNE ?= "cortexa32"

TUNEVALID[cortexa32] = "Enable Cortex-A32 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa32', ' -mcpu=cortex-a32', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# On AArch32 the ARMv8 crypto extensions (AES/SHA) live in the FPU, not just
# -march. OE-Core's 'crypto' feature only adds '+crypto' to -march, leaving the
# FPU at plain 'neon', so GCC refuses to inline the AES intrinsics used by e.g.
# mbedtls' aesce.c ("target specific option mismatch"). Select the crypto FPU by
# making it the last word of TUNE_CCARGS_MFPU - the same idiom OE-Core uses for
# the vfpv4+neon -> neon-vfpv4 combination in feature-arm-neon.inc.
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', [ 'crypto', 'neon' ], ' crypto-neon-fp-armv8', '', d)}"

# Little Endian base configs
AVAILTUNES += "cortexa32 cortexa32-crypto"
ARMPKGARCH:tune-cortexa32             = "cortexa32"
ARMPKGARCH:tune-cortexa32-crypto      = "cortexa32"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa32          = "arm armv8a cortexa32 crc callconvention-hard neon thumb"
TUNE_FEATURES:tune-cortexa32-crypto   = "${TUNE_FEATURES:tune-cortexa32} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa32             = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa32 cortexa32t2hf-neon"
PACKAGE_EXTRA_ARCHS:tune-cortexa32-crypto      = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa32 cortexa32t2hf-neon cortexa32t2hf-crypto-neon-fp-armv8"
BASE_LIB:tune-cortexa32               = "lib"
BASE_LIB:tune-cortexa32-crypto        = "lib"
