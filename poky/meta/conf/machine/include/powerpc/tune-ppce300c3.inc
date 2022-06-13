DEFAULTTUNE ?= "ppce300c3"

require conf/machine/include/powerpc/arch-powerpc.inc

AVAILTUNES += "ppce300c3 ppce300c3-nf"

# hard-float
TUNEVALID[ppce300c3] = "Enable ppce300c3 specific processor optimizations"
TUNE_FEATURES:tune-ppce300c3 = "${TUNE_FEATURES:tune-powerpc} ppce300c3"
TUNE_PKGARCH:tune-ppce300c3 = "ppce300c3"
PACKAGE_EXTRA_ARCHS:tune-ppce300c3 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppce300c3"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce300c3', ' -mcpu=e300c3', '', d)}"

# soft-float
TUNEVALID[ppce300c3-nf] = "Enable ppce300c3 specific processor optimizations (no fpu)"
TUNE_FEATURES:tune-ppce300c3-nf = "${TUNE_FEATURES:tune-powerpc-nf} ppce300c3-nf"
TUNE_PKGARCH:tune-ppce300c3-nf = "ppce300c3-nf"
PACKAGE_EXTRA_ARCHS:tune-ppce300c3-nf = "${PACKAGE_EXTRA_ARCHS:tune-powerpc-nf} ppce300c3-nf"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce300c3-nf', ' -mcpu=e300c3', '', d)}"

