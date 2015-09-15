# Settings for the GCC(1) cpu-type "corei7":
#
#     Intel Core i7 CPU with 64-bit extensions, MMX, SSE, SSE2, SSE3, SSSE3, SSE4.1
#     and SSE4.2 instruction set support.
#
# This tune is recommended for Intel Nehalem and Silvermont (e.g. Bay Trail) CPUs
# (and beyond).
#
DEFAULTTUNE ?= "corei7-64"

# Pull in the previous tune in to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/tune-core2.inc

# Extra tune features
TUNEVALID[corei7] = "Enable corei7 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "corei7", " -march=corei7 -mtune=corei7 -mfpmath=sse -msse4.2", "", d)}"

# Extra tune selections
AVAILTUNES += "corei7-32"
TUNE_FEATURES_tune-corei7-32 = "${TUNE_FEATURES_tune-x86} corei7"
BASE_LIB_tune-corei7-32 = "lib"
TUNE_PKGARCH_tune-corei7-32 = "corei7-32"
PACKAGE_EXTRA_ARCHS_tune-corei7-32 = "${PACKAGE_EXTRA_ARCHS_tune-core2} corei7-32"

AVAILTUNES += "corei7-64"
TUNE_FEATURES_tune-corei7-64 = "${TUNE_FEATURES_tune-x86-64} corei7"
BASE_LIB_tune-corei7-64 = "lib64"
TUNE_PKGARCH_tune-corei7-64 = "corei7-64"
PACKAGE_EXTRA_ARCHS_tune-corei7-64 = "${PACKAGE_EXTRA_ARCHS_tune-core2-64} corei7-64"

AVAILTUNES += "corei7-64-x32"
TUNE_FEATURES_tune-corei7-64-x32 = "${TUNE_FEATURES_tune-x86-64-x32} corei7"
BASE_LIB_tune-corei7-64-x32 = "libx32"
TUNE_PKGARCH_tune-corei7-64-x32 = "corei7-64-x32"
PACKAGE_EXTRA_ARCHS_tune-corei7-64-x32 = "${PACKAGE_EXTRA_ARCHS_tune-core2-64-x32} corei7-64-x32"
