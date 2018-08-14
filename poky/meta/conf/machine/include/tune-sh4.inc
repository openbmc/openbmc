DEFAULTTUNE ?= "sh4"

# Pull in sh4 for compatibility...
require conf/machine/include/sh/arch-sh.inc

TUNEVALID[sh4] = "Enable SH4 optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'sh4', ' -m4', '', d)}"

# NOTE: If you want to optimize to sh4a, conf/machine/include/tune-sh4a.inc.
# But it is not compatible for sh4.
# The binary optimized by m4a doesn't operate on sh4. It works on sh4a only.
TUNEVALID[sh4a] = "Enable SH4a optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'sh4a', ' -m4a', '', d)}"

AVAILTUNES += "sh4 sh4eb sh4a sh4aeb"
TUNE_FEATURES_tune-sh4 = "sh4"
TUNE_ARCH_tune-sh4 = "sh4"
TUNE_PKGARCH_tune-sh4 = "sh4"
PACKAGE_EXTRA_ARCHS_tune-sh4 = "sh sh4"

TUNE_FEATURES_tune-sh4eb = "sh4 bigendian"
TUNE_ARCH_tune-sh4eb = "sh4eb"
TUNE_PKGARCH_tune-sh4eb = "sh4eb"
PACKAGE_EXTRA_ARCHS_tune-sh4eb = "sheb sh4eb"

TUNE_FEATURES_tune-sh4a = "sh4a"
TUNE_ARCH_tune-sh4a = "sh4"
TUNE_PKGARCH_tune-sh4a = "sh4a"
PACKAGE_EXTRA_ARCHS_tune-sh4a = "sh sh4 sh4a"

TUNE_FEATURES_tune-sh4aeb = "sh4a bigendian"
TUNE_ARCH_tune-sh4aeb = "sh4eb"
TUNE_PKGARCH_tune-sh4aeb = "sh4aeb"
PACKAGE_EXTRA_ARCHS_tune-sh4aeb = "sheb sh4eb sh4aeb"
