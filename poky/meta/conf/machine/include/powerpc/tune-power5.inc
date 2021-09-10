DEFAULTTUNE ?= "ppcp5"

require conf/machine/include/powerpc/arch-powerpc64.inc

TUNEVALID[power5] = "Enable IBM Power5 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'power5', ' -mcpu=power5', '', d)}"

AVAILTUNES += "ppcp5 ppc64p5"
TUNE_FEATURES:tune-ppcp5 = "m32 fpu-hard power5 altivec bigendian"
BASE_LIB:tune-ppcp5 = "lib"
TUNE_PKGARCH:tune-ppcp5 = "ppcp5"
PACKAGE_EXTRA_ARCHS:tune-ppcp5 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc} ppcp5"

TUNE_FEATURES:tune-ppc64p5 = "m64 fpu-hard power5 altivec bigendian"
BASE_LIB:tune-ppc64p5 = "lib64"
TUNE_PKGARCH:tune-ppc64p5 = "ppc64p5"
PACKAGE_EXTRA_ARCHS:tune-ppc64p5 = "${PACKAGE_EXTRA_ARCHS:tune-powerpc64} ppc64p5"

# glibc configure options to get power5 specific library
GLIBC_EXTRA_OECONF:powerpc64 += "${@bb.utils.contains('TUNE_FEATURES', 'power5', '--with-cpu=power5', '', d)}"
GLIBC_EXTRA_OECONF:powerpc += "${@bb.utils.contains('TUNE_FEATURES', 'power5', '--with-cpu=power5', '', d)}"

# QEMU usermode fails with invalid instruction error
MACHINE_FEATURES_BACKFILL_CONSIDERED:append = "${@bb.utils.contains('TUNE_FEATURES', 'power5', ' qemu-usermode', '', d)}"
