DEFAULTTUNE ?= "ppc64p8le"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power8] = "Enable IBM Power8 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power8', ' -mcpu=power8', '', d)}"

AVAILTUNES += "ppcp8 ppc64p8 ppcp8le ppc64p8le"

TUNE_FEATURES:tune-ppcp8 = "m32 fpu-hard power8 altivec bigendian"
BASE_LIB:tune-ppcp8 = "lib"
TUNE_PKGARCH:tune-ppcp8 = "ppcp8"
PACKAGE_EXTRA_ARCHS:tune-ppcp8 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppcp8"

TUNE_FEATURES:tune-ppc64p8 = "m64 fpu-hard power8 altivec bigendian"
BASE_LIB:tune-ppc64p8 = "lib64"
TUNE_PKGARCH:tune-ppc64p8 = "ppc64p8"
PACKAGE_EXTRA_ARCHS:tune-ppc64p8 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64} ppc64p8"

TUNE_FEATURES:tune-ppcp8le = "m32 fpu-hard power8 altivec"
BASE_LIB:tune-ppcp8le = "lib"
TUNE_PKGARCH:tune-ppcp8le = "ppcp8le"
PACKAGE_EXTRA_ARCHS:tune-ppcp8le = "${PACKAGE_EXTRA_ARCHS:tune-powerpcle} ppcp8le"

TUNE_FEATURES:tune-ppc64p8le = "m64 fpu-hard power8 altivec"
BASE_LIB:tune-ppc64p8le = "lib64"
TUNE_PKGARCH:tune-ppc64p8le = "ppc64p8le"
PACKAGE_EXTRA_ARCHS:tune-ppc64p8le = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64le} ppc64p8le"

# glibc configure options to get power8 specific library
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'power8', '--with-cpu=power8', '', d)}"
