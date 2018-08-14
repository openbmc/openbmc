DEFAULTTUNE ?= "ppce6500"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[e6500] = "Enable Freescale e6500 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'e6500', ' -mcpu=e6500', '', d)}"

AVAILTUNES += "ppce6500 ppc64e6500"
TUNE_FEATURES_tune-ppce6500 = "m32 fpu-hard e6500 altivec"
BASE_LIB_tune-ppce6500 = "lib"
TUNE_PKGARCH_tune-ppce6500 = "ppce6500"
PACKAGE_EXTRA_ARCHS_tune-ppce6500 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppce6500"

TUNE_FEATURES_tune-ppc64e6500 = "m64 fpu-hard e6500 altivec"
BASE_LIB_tune-ppc64e6500 = "lib64"
TUNE_PKGARCH_tune-ppc64e6500 = "ppc64e6500"
PACKAGE_EXTRA_ARCHS_tune-ppc64e6500 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64e6500"

# glibc configure options to get e6500 specific library
GLIBC_EXTRA_OECONF_powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'e6500', '--with-cpu=e6500', '', d)}"
GLIBC_EXTRA_OECONF_powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'e6500', '--with-cpu=e6500', '', d)}"

# QEMU usermode fails with invalid instruction error (YOCTO: #10304)
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'e6500', ' qemu-usermode', '', d)}"
