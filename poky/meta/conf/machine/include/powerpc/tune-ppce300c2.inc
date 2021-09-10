DEFAULTTUNE ?= "ppce300c2"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppce300c2] = "Enable ppce300c2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce300c2', ' -mcpu=e300c2', '', d)}"

AVAILTUNES += "ppce300c2"
TUNE_FEATURES:tune-ppce300c2 = "m32 fpu-soft ppce300c2 bigendian"
TUNE_PKGARCH:tune-ppce300c2 = "ppce300c2"
PACKAGE_EXTRA_ARCHS:tune-ppce300c2 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc-nf} ppce300c2"
