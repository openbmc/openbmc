DEFAULTTUNE ?= "ppc476"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppc476] = "Enable ppc476 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppc476', ' -mcpu=476', '', d)}"

AVAILTUNES += "ppc476"
TUNE_FEATURES:tune-ppc476 = "m32 fpu-hard ppc476 bigendian"
TUNE_PKGARCH:tune-ppc476 = "ppc476"
PACKAGE_EXTRA_ARCHS:tune-ppc476 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppc476"

# glibc configure options to get 476 specific library (for sqrt)
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'ppc476', '-with-cpu=476', '', d)}"
