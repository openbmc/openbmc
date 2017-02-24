DEFAULTTUNE ?= "ppce5500"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[e5500] = "Enable Freescale e5500 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'e5500', ' -mcpu=e5500', '', d)}"

AVAILTUNES += "ppce5500 ppc64e5500"
TUNE_FEATURES_tune-ppce5500 = "m32 fpu-hard e5500"
BASE_LIB_tune-ppce5500 = "lib"
TUNE_PKGARCH_tune-ppce5500 = "ppce5500"
PACKAGE_EXTRA_ARCHS_tune-ppce5500 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppce5500"

TUNE_FEATURES_tune-ppc64e5500 = "m64 fpu-hard e5500"
BASE_LIB_tune-ppc64e5500 = "lib64"
TUNE_PKGARCH_tune-ppc64e5500 = "ppc64e5500"
PACKAGE_EXTRA_ARCHS_tune-ppc64e5500 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc64} ppc64e5500"

# glibc configure options to get e5500 specific library (for sqrt)
GLIBC_EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'e5500', '--with-cpu=e5500', '', d)}"

# QEMU usermode fails with invalid instruction error (YOCTO: #10304)
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'e5500', ' qemu-usermode', '', d)}"
