DEFAULTTUNE ?= "ppcp6"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power6] = "Enable IBM Power6 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power6', ' -mcpu=power6', '', d)}"

AVAILTUNES += "ppcp6 ppc64p6"
TUNE_FEATURES_tune-ppcp6 = "m32 fpu-hard power6 altivec bigendian"
BASE_LIB_tune-ppcp6 = "lib"
TUNE_PKGARCH_tune-ppcp6 = "ppcp6"
PACKAGE_EXTRA_ARCHS_tune-ppcp6 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppcp6"

TUNE_FEATURES_tune-ppc64p6 = "m64 fpu-hard power6 altivec bigendian"
BASE_LIB_tune-ppc64p6 = "lib64"
TUNE_PKGARCH_tune-ppc64p6 = "ppc64p6"
PACKAGE_EXTRA_ARCHS_tune-ppc64p6 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64p6"

# glibc configure options to get power6 specific library
GLIBC_EXTRA_OECONF_powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'power6', '--with-cpu=power6', '', d)}"
GLIBC_EXTRA_OECONF_powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'power6', '--with-cpu=power6', '', d)}"

# QEMU usermode fails with invalid instruction error
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'power6', ' qemu-usermode', '', d)}"
