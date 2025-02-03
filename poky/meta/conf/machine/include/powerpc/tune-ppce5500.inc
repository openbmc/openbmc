DEFAULTTUNE ?= "ppce5500"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[e5500] = "Enable Freescale e5500 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'e5500', ' -mcpu=e5500', '', d)}"

AVAILTUNES += "ppce5500 ppc64e5500"
TUNE_FEATURES:tune-ppce5500 = "m32 fpu-hard e5500 bigendian"
BASE_LIB:tune-ppce5500 = "lib"
TUNE_PKGARCH:tune-ppce5500 = "ppce5500"
PACKAGE_EXTRA_ARCHS:tune-ppce5500 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppce5500"
QEMU_EXTRAOPTIONS:tune-ppce5500 = " -cpu e500mc"

TUNE_FEATURES:tune-ppc64e5500 = "m64 fpu-hard e5500 bigendian"
BASE_LIB:tune-ppc64e5500 = "lib64"
TUNE_PKGARCH:tune-ppc64e5500 = "ppc64e5500"
PACKAGE_EXTRA_ARCHS:tune-ppc64e5500 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64} ppc64e5500"
QEMU_EXTRAOPTIONS:tune-ppc64e5500 = " -cpu e500mc"

# QEMU usermode fails with invalid instruction error (YOCTO: #10304)
MACHINE_FEATURES_BACKFILL_CONSIDERED:append = "${@bb.utils.contains('TUNE_FEATURES', 'e5500', ' qemu-usermode', '', d)}"
