DEFAULTTUNE ?= "ppc603e"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppc603e] = "Enable ppc603e specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppc603e', ' -mcpu=603e', '', d)}"

AVAILTUNES += "ppc603e"
TUNE_FEATURES:tune-ppc603e = "m32 fpu-hard ppc603e bigendian"
TUNE_PKGARCH:tune-ppc603e = "ppc603e"
PACKAGE_EXTRA_ARCHS:tune-ppc603e = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppc603e"
