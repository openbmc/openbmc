# Settings for the GCC(1) cpu-type "core2":
#
#      Intel Core2 CPU with 64-bit extensions, MMX, SSE, SSE2, SSE3 and SSSE3
#      instruction set support.
#
# This tune is recommended for the Intel Core 2 CPU family, including Conroe,
# Merom and beyond, as well as the first Atom CPUs, Diamondville, and beyond.
#
DEFAULTTUNE ?= "core2-32"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/x86/tune-i686.inc

# Extra tune features
TUNEVALID[core2] = "Enable core2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'core2', ' -march=core2 -mtune=core2 -msse3 -mfpmath=sse', '', d)}"

# Extra tune selections
AVAILTUNES += "core2-32"
TUNE_FEATURES:tune-core2-32 = "${TUNE_FEATURES:tune-x86} core2"
BASE_LIB:tune-core2-32 = "lib"
TUNE_PKGARCH:tune-core2-32 = "core2-32"
PACKAGE_EXTRA_ARCHS:tune-core2-32 = "${PACKAGE_EXTRA_ARCHS:tune-i686} core2-32"
QEMU_EXTRAOPTIONS_core2-32 = " -cpu n270"

AVAILTUNES += "core2-64"
TUNE_FEATURES:tune-core2-64 = "${TUNE_FEATURES:tune-x86-64} core2"
BASE_LIB:tune-core2-64 = "lib64"
TUNE_PKGARCH:tune-core2-64 = "core2-64"
PACKAGE_EXTRA_ARCHS:tune-core2-64 = "${PACKAGE_EXTRA_ARCHS:tune-x86-64} core2-64"
QEMU_EXTRAOPTIONS_core2-64 = " -cpu core2duo"

AVAILTUNES += "core2-64-x32"
TUNE_FEATURES:tune-core2-64-x32 = "${TUNE_FEATURES:tune-x86-64-x32} core2"
BASE_LIB:tune-core2-64-x32 = "libx32"
TUNE_PKGARCH:tune-core2-64-x32 = "core2-64-x32"
PACKAGE_EXTRA_ARCHS:tune-core2-64-x32 = "${PACKAGE_EXTRA_ARCHS:tune-x86-64-x32} core2-64-x32"
QEMU_EXTRAOPTIONS_core2-64-x32 = " -cpu core2duo"
