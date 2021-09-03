# Settings for the GCC(1) cpu-type "nehalem":
#
#     Intel Nehalem CPU with 64-bit extensions, MMX, SSE, SSE2, SSE3, SSSE3, SSE4.1,
#     SSE4.2 and POPCNT instruction set support.
#
# This tune is recommended for Intel Nehalem and Silvermont (e.g. Bay Trail) CPUs
# (and beyond).
#
DEFAULTTUNE ?= "corei7-64"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/x86/tune-core2.inc

# Extra tune features
TUNEVALID[corei7] = "Enable corei7 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'corei7', ' -march=nehalem -mtune=generic -mfpmath=sse -msse4.2', '', d)}"

# Extra tune selections
AVAILTUNES += "corei7-32"
TUNE_FEATURES:tune-corei7-32 = "${TUNE_FEATURES:tune-x86} corei7"
BASE_LIB:tune-corei7-32 = "lib"
TUNE_PKGARCH:tune-corei7-32 = "corei7-32"
PACKAGE_EXTRA_ARCHS:tune-corei7-32 = "${PACKAGE_EXTRA_ARCHS:tune-core2-32} corei7-32"
QEMU_EXTRAOPTIONS_corei7-32 = " -cpu Nehalem,check=false"

AVAILTUNES += "corei7-64"
TUNE_FEATURES:tune-corei7-64 = "${TUNE_FEATURES:tune-x86-64} corei7"
BASE_LIB:tune-corei7-64 = "lib64"
TUNE_PKGARCH:tune-corei7-64 = "corei7-64"
PACKAGE_EXTRA_ARCHS:tune-corei7-64 = "${PACKAGE_EXTRA_ARCHS:tune-core2-64} corei7-64"
QEMU_EXTRAOPTIONS_corei7-64 = " -cpu Nehalem,check=false"

AVAILTUNES += "corei7-64-x32"
TUNE_FEATURES:tune-corei7-64-x32 = "${TUNE_FEATURES:tune-x86-64-x32} corei7"
BASE_LIB:tune-corei7-64-x32 = "libx32"
TUNE_PKGARCH:tune-corei7-64-x32 = "corei7-64-x32"
PACKAGE_EXTRA_ARCHS:tune-corei7-64-x32 = "${PACKAGE_EXTRA_ARCHS:tune-core2-64-x32} corei7-64-x32"
QEMU_EXTRAOPTIONS_corei7-64-x32 = " -cpu Nehalem,check=false"
