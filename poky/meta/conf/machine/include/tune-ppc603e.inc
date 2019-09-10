DEFAULTTUNE ?= "ppc603e"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppc603e] = "Enable ppc603e specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppc603e', ' -mcpu=603e', '', d)}"

AVAILTUNES += "ppc603e"
TUNE_FEATURES_tune-ppc603e = "m32 fpu-hard ppc603e bigendian"
TUNE_PKGARCH_tune-ppc603e = "ppc603e"
PACKAGE_EXTRA_ARCHS_tune-ppc603e = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppc603e"

# glibc configure options to get 603e specific library (for sqrt)
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'ppc603e', '-with-cpu=603e', '', d)}"
