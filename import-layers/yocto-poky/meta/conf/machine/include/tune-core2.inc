# Settings for the GCC(1) cpu-type "core2":
#
#      Intel Core2 CPU with 64-bit extensions, MMX, SSE, SSE2, SSE3 and SSSE3
#      instruction set support.
#
# This tune is recommended for the Intel Core 2 CPU family, including Conroe,
# Merom and beyond, as well as the first Atom CPUs, Diamondville, and beyond.
#
DEFAULTTUNE ?= "core2-32"

# Set x86 target arch to i686, so that glibc enables SSE optimised memcpy, etc.
X86ARCH32 ?= "i686"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/tune-i586.inc

# Extra tune features
TUNEVALID[core2] = "Enable core2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'core2', ' -march=core2 -mtune=core2 -msse3 -mfpmath=sse', '', d)}"

# Extra tune selections
AVAILTUNES += "core2-32"
TUNE_FEATURES_tune-core2-32 = "${TUNE_FEATURES_tune-x86} core2"
BASE_LIB_tune-core2-32 = "lib"
TUNE_PKGARCH_tune-core2-32 = "core2-32"
PACKAGE_EXTRA_ARCHS_tune-core2-32 = "${PACKAGE_EXTRA_ARCHS_tune-i586} core2-32"

AVAILTUNES += "core2-64"
TUNE_FEATURES_tune-core2-64 = "${TUNE_FEATURES_tune-x86-64} core2"
BASE_LIB_tune-core2-64 = "lib64"
TUNE_PKGARCH_tune-core2-64 = "core2-64"
PACKAGE_EXTRA_ARCHS_tune-core2-64 = "${PACKAGE_EXTRA_ARCHS_tune-x86-64} core2-64"

AVAILTUNES += "core2-64-x32"
TUNE_FEATURES_tune-core2-64-x32 = "${TUNE_FEATURES_tune-x86-64-x32} core2"
BASE_LIB_tune-core2-64-x32 = "libx32"
TUNE_PKGARCH_tune-core2-64-x32 = "core2-64-x32"
PACKAGE_EXTRA_ARCHS_tune-core2-64-x32 = "${PACKAGE_EXTRA_ARCHS_tune-x86-64-x32} core2-64-x32"
