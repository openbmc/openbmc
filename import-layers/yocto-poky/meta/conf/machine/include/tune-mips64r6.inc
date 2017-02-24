DEFAULTTUNE ?= "mipsisa64r6el"

require conf/machine/include/mips/arch-mips.inc

TUNEVALID[mipsisa64r6] = "Enable mipsisa64r6 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mipsisa64r6', ' -march=mips64r6', '', d)}"

# Base Tunes
AVAILTUNES += "mipsisa64r6 mipsisa64r6el"

TUNE_FEATURES_tune-mipsisa64r6 = "bigendian fpu-hard n64 mipsisa64r6 r6"
MIPSPKGSFX_VARIANT_tune-mipsisa64r6 = "${TUNE_ARCH}"
BASE_LIB_tune-mipsisa64r6 = "lib64"
PACKAGE_EXTRA_ARCHS_tune-mipsisa64r6 = "mipsisa64r6"

TUNE_FEATURES_tune-mipsisa64r6el = "fpu-hard r6 n64 mipsisa64r6"
MIPSPKGSFX_VARIANT_tune-mipsisa64r6el = "${TUNE_ARCH}"
BASE_LIB_tune-mipsisa64r6el = "lib64"
PACKAGE_EXTRA_ARCHS_tune-mipsisa64r6el = "mipsisa64r6el"

# Soft Float
AVAILTUNES += "mipsisa64r6-nf mipsisa64r6el-nf"

TUNE_FEATURES_tune-mipsisa64r6-nf = "bigendian r6 n64 mipsisa64r6"
MIPSPKGSFX_VARIANT_tune-mipsisa64r6-nf = "${TUNE_ARCH}"
BASE_LIB_tune-mipsisa64r6-nf = "lib64"
PACKAGE_EXTRA_ARCHS_tune-mipsisa64r6-nf = "mipsisa64r6"

TUNE_FEATURES_tune-mipsisa64r6el-nf = "r6 n64 mipsisa64r6"
MIPSPKGSFX_VARIANT_tune-mipsisa64r6el-nf = "${TUNE_ARCH}"
BASE_LIB_tune-mipsisa64r6el-nf = "lib64"
PACKAGE_EXTRA_ARCHS_tune-mipsisa64r6el-nf = "mipsisa64r6el-nf"
