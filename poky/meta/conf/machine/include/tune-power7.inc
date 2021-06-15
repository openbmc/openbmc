DEFAULTTUNE ?= "ppcp7"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power7] = "Enable IBM Power7 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power7', ' -mcpu=power7', '', d)}"

AVAILTUNES += "ppcp7 ppc64p7"
TUNE_FEATURES_tune-ppcp7 = "m32 fpu-hard power7 altivec bigendian"
BASE_LIB_tune-ppcp7 = "lib"
TUNE_PKGARCH_tune-ppcp7 = "ppcp7"
PACKAGE_EXTRA_ARCHS_tune-ppcp7 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppcp7"

TUNE_FEATURES_tune-ppc64p7 = "m64 fpu-hard power7 altivec bigendian"
BASE_LIB_tune-ppc64p7 = "lib64"
TUNE_PKGARCH_tune-ppc64p7 = "ppc64p7"
PACKAGE_EXTRA_ARCHS_tune-ppc64p7 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64p7"

# glibc configure options to get power7 specific library
GLIBC_EXTRA_OECONF_powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'power7', '--with-cpu=power7', '', d)}"
GLIBC_EXTRA_OECONF_powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'power7', '--with-cpu=power7', '', d)}"

# QEMU usermode fails with invalid instruction error
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'power7', ' qemu-usermode', '', d)}"
