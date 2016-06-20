DEFAULTTUNE ?= "ppc64p5"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power5] = "Enable IBM Power5 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power5', ' -mcpu=power5', '', d)}"

AVAILTUNES += "ppcp5 ppc64p5"
TUNE_FEATURES_tune-ppcp5 = "m32 fpu-hard power5 altivec"
BASE_LIB_tune-ppcp5 = "lib"
TUNE_PKGARCH_tune-ppcp5 = "ppcp5"
PACKAGE_EXTRA_ARCHS_tune-ppcp5 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppcp5"

TUNE_FEATURES_tune-ppc64p5 = "m64 fpu-hard power5 altivec"
BASE_LIB_tune-ppc64p5 = "lib64"
TUNE_PKGARCH_tune-ppc64p5 = "ppc64p5"
PACKAGE_EXTRA_ARCHS_tune-ppc64p5 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64p5"

# glibc configure options to get power5 specific library
GLIBC_EXTRA_OECONF_powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'power5', '--with-cpu=power5', '', d)}"
GLIBC_EXTRA_OECONF_powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'power5', '--with-cpu=power5', '', d)}"
