DEFAULTTUNE ?= "sh3"

require conf/machine/include/sh/arch-sh.inc

TUNEVALID[sh3] = "Enable SH3 optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'sh3', ' -m3', '', d)}"

AVAILTUNES += "sh3 sh3eb"
TUNE_FEATURES:tune-sh3 = "sh3"
TUNE_ARCH:tune-sh3 = "sh3"
TUNE_PKGARCH:tune-sh3 = "sh3"
PACKAGE_EXTRA_ARCHS:tune-sh3 = "sh sh3"

TUNE_FEATURES:tune-sh3eb = "sh3 bigendian"
TUNE_ARCH:tune-sh3eb = "sh3eb"
TUNE_PKGARCH:tune-sh3eb = "sh3eb"
PACKAGE_EXTRA_ARCHS:tune-sh3eb = "sheb sh3eb"
