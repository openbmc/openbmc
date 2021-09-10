DEFAULTTUNE ?= "ppc64p9le"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power9] = "Enable IBM Power9 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power9', ' -mcpu=power9', '', d)}"

AVAILTUNES += "ppcp9 ppc64p9 ppcp9le ppc64p9le"

TUNE_FEATURES:tune-ppcp9 = "m32 fpu-hard power9 altivec bigendian"
BASE_LIB:tune-ppcp9 = "lib"
TUNE_PKGARCH:tune-ppcp9 = "ppcp9"
PACKAGE_EXTRA_ARCHS:tune-ppcp9 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppcp9"

TUNE_FEATURES:tune-ppc64p9 = "m64 fpu-hard power9 altivec bigendian"
BASE_LIB:tune-ppc64p9 = "lib64"
TUNE_PKGARCH:tune-ppc64p9 = "ppc64p9"
PACKAGE_EXTRA_ARCHS:tune-ppc64p9 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64} ppc64p9"

TUNE_FEATURES:tune-ppcp9le = "m32 fpu-hard power9 altivec"
BASE_LIB:tune-ppcp9le = "lib"
TUNE_PKGARCH:tune-ppcp9le = "ppcp9le"
PACKAGE_EXTRA_ARCHS:tune-ppcp9le = "${PACKAGE_EXTRA_ARCHS:tune-powerpcle} ppcp9le"

TUNE_FEATURES:tune-ppc64p9le = "m64 fpu-hard power9 altivec"
BASE_LIB:tune-ppc64p9le = "lib64"
TUNE_PKGARCH:tune-ppc64p9le = "ppc64p9le"
PACKAGE_EXTRA_ARCHS:tune-ppc64p9le = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64le} ppc64p9le"

# glibc configure options to get power9 specific library
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'power9', '--with-cpu=power9', '', d)}"
