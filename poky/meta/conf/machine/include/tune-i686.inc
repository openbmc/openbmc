#
# The i686 CPU family was introduced with the Intel Pentium Pro in 1995.
# It has as required feature flags: fpu tsc cx8 cmov.
#
# It is the minimum CPU required by the Debian i386 port.
# https://lists.debian.org/debian-devel-announce/2016/05/msg00001.html
#
#

DEFAULTTUNE ?= "i686"

# Set x86 target arch to i686, so that glibc enables SSE optimised memcpy, etc.
X86ARCH32 ?= "i686"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/tune-i586.inc

# Extra tune features
TUNEVALID[i686] = "Enable i686 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'i686', ' -march=i686', '', d)}"

# Extra tune selections
AVAILTUNES += "i686"
TUNE_FEATURES_tune-i686 = "${TUNE_FEATURES_tune-x86} i686"
BASE_LIB_tune-i686 = "lib"
TUNE_PKGARCH_tune-i686 = "i686"
PACKAGE_EXTRA_ARCHS_tune-i686 = "${PACKAGE_EXTRA_ARCHS_tune-i586} i686"
