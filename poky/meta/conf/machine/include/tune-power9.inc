DEFAULTTUNE ?= "ppc64p9le"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power9] = "Enable IBM Power9 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power9', ' -mcpu=power9', '', d)}"

AVAILTUNES += "ppcp9 ppc64p9 ppcp9le ppc64p9le"

TUNE_FEATURES_tune-ppcp9 = "m32 fpu-hard power9 altivec bigendian"
BASE_LIB_tune-ppcp9 = "lib"
TUNE_PKGARCH_tune-ppcp9 = "ppcp9"
PACKAGE_EXTRA_ARCHS_tune-ppcp9 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppcp9"

TUNE_FEATURES_tune-ppc64p9 = "m64 fpu-hard power9 altivec bigendian"
BASE_LIB_tune-ppc64p9 = "lib64"
TUNE_PKGARCH_tune-ppc64p9 = "ppc64p9"
PACKAGE_EXTRA_ARCHS_tune-ppc64p9 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64p9"

TUNE_FEATURES_tune-ppcp9le = "m32 fpu-hard power9 altivec"
BASE_LIB_tune-ppcp9le = "lib"
TUNE_PKGARCH_tune-ppcp9le = "ppcp9le"
PACKAGE_EXTRA_ARCHS_tune-ppcp9le = "${PACKAGE_EXTRA_ARCHS_tune-powerpcle} ppcp9le"

TUNE_FEATURES_tune-ppc64p9le = "m64 fpu-hard power9 altivec"
BASE_LIB_tune-ppc64p9le = "lib64"
TUNE_PKGARCH_tune-ppc64p9le = "ppc64p9le"
PACKAGE_EXTRA_ARCHS_tune-ppc64p9le = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64le} ppc64p9le"

# glibc configure options to get power9 specific library
GLIBC_EXTRA_OECONF_powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'power9', '--with-cpu=power9', '', d)}"
GLIBC_EXTRA_OECONF_powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'power9', '--with-cpu=power9', '', d)}"

# QEMU usermode fails with invalid instruction error
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'power9', ' qemu-usermode', '', d)}"
