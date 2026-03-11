# Settings for the GCC(1) cpu-type "x86-64-v3":
#
#     CPUs with AVX, AVX2, BMI1, BMI2, F16C, FMA, LZCNT, MOVBE, XSAVE.
#     (but not AVX512).
#     See https://www.phoronix.com/news/GCC-11-x86-64-Feature-Levels for details.
#
# This tune is recommended for Intel Haswell/AMD Excavator CPUs (and later).
#
DEFAULTTUNE ?= "x86-64-v3"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/x86/tune-corei7.inc

# Extra tune features
TUNEVALID[x86-64-v3] = "Enable x86-64-v3 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'x86-64-v3', ' -march=x86-64-v3', '', d)}"

# Extra tune selections
AVAILTUNES += "x86-64-v3"
TUNE_FEATURES:tune-x86-64-v3 = "${TUNE_FEATURES:tune-x86-64} x86-64-v3"
BASE_LIB:tune-x86-64-v3 = "lib64"
TUNE_PKGARCH:tune-x86-64-v3 = "x86-64-v3"
PACKAGE_EXTRA_ARCHS:tune-x86-64-v3 = "${PACKAGE_EXTRA_ARCHS:tune-corei7-64} x86-64-v3"
QEMU_EXTRAOPTIONS:tune-x86-64-v3 = " -cpu Skylake-Client,check=false"

AVAILTUNES += "x86-64-v3-x32"
TUNE_FEATURES:tune-x86-64-v3-x32 = "${TUNE_FEATURES:tune-x86-64-x32} x86-64-v3"
BASE_LIB:tune-x86-64-v3-x32 = "libx32"
TUNE_PKGARCH:tune-x86-64-v3-x32 = "x86-64-v3-x32"
PACKAGE_EXTRA_ARCHS:tune-x86-64-v3-x32 = "${PACKAGE_EXTRA_ARCHS:tune-corei7-64-x32} x86-64-v3-x32"
QEMU_EXTRAOPTIONS:tune-x86-64-v3-x32 = " -cpu Skylake-Client,check=false"
