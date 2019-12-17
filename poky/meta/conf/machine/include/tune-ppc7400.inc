DEFAULTTUNE ?= "ppc7400"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppc7400] = "Enable ppc7400 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppc7400', ' -mcpu=7400 -mno-spe', '', d)}"

AVAILTUNES += "ppc7400"
TUNE_FEATURES_tune-ppc7400 = "m32 fpu-hard ppc7400 altivec bigendian"
TUNE_PKGARCH_tune-ppc7400 = "ppc7400"
PACKAGE_EXTRA_ARCHS_tune-ppc7400 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppc7400"

# glibc configure options to get 7400 specific library (for sqrt)
#GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'ppc7400', '--with-cpu=power4', '', d)}"
