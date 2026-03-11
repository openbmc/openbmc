DEFAULTTUNE ?= "ppc7400"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppc7400] = "Enable ppc7400 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppc7400', ' -mcpu=7400 -mno-spe', '', d)}"

AVAILTUNES += "ppc7400"
TUNE_FEATURES:tune-ppc7400 = "m32 fpu-hard ppc7400 altivec bigendian"
TUNE_PKGARCH:tune-ppc7400 = "ppc7400"
PACKAGE_EXTRA_ARCHS:tune-ppc7400 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppc7400"
QEMU_EXTRAOPTIONS:tune-ppc7400 = " -cpu 7400"
