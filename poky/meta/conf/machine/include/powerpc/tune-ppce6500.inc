DEFAULTTUNE ?= "ppce6500"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[e6500] = "Enable Freescale e6500 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'e6500', ' -mcpu=e6500', '', d)}"

AVAILTUNES += "ppce6500 ppc64e6500"
TUNE_FEATURES:tune-ppce6500 = "m32 fpu-hard e6500 altivec bigendian"
BASE_LIB:tune-ppce6500 = "lib"
TUNE_PKGARCH:tune-ppce6500 = "ppce6500"
PACKAGE_EXTRA_ARCHS:tune-ppce6500 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppce6500"
QEMU_EXTRAOPTIONS:tune-ppce6500 = " -cpu e500mc"

TUNE_FEATURES:tune-ppc64e6500 = "m64 fpu-hard e6500 altivec bigendian"
BASE_LIB:tune-ppc64e6500 = "lib64"
TUNE_PKGARCH:tune-ppc64e6500 = "ppc64e6500"
PACKAGE_EXTRA_ARCHS:tune-ppc64e6500 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64} ppc64e6500"
QEMU_EXTRAOPTIONS:tune-ppc64e6500 = " -cpu e500mc"

# QEMU usermode fails with invalid instruction error (YOCTO: #10304)
MACHINE_FEATURES_BACKFILL_CONSIDERED:append = "${@bb.utils.contains('TUNE_FEATURES', 'e6500', ' qemu-usermode', '', d)}"
