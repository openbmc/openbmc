DEFAULTTUNE ?= "mips32r2"

require conf/machine/include/tune-mips32.inc

TUNEVALID[mips32r2] = "Enable mips32r2 specific processor optimizations"
TUNECONFLICTS[mips32r2] = "n64 n32"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mips32r2', ' -march=mips32r2', '', d)}"

# Base Tunes (Hard Float)
AVAILTUNES += "mips32r2 mips32r2el"

TUNE_FEATURES_tune-mips32r2 = "${TUNE_FEATURES_tune-mips} mips32r2"
MIPSPKGSFX_VARIANT_tune-mips32r2 = "mips32r2"
PACKAGE_EXTRA_ARCHS_tune-mips32r2 = "mips mips32 mips32r2"

TUNE_FEATURES_tune-mips32r2el = "${TUNE_FEATURES_tune-mipsel} mips32r2"
MIPSPKGSFX_VARIANT_tune-mips32r2el = "mips32r2el"
PACKAGE_EXTRA_ARCHS_tune-mips32r2el = "mipsel mips32el mips32r2el"

# Soft Float
AVAILTUNES += "mips32r2-nf mips32r2el-nf"

TUNE_FEATURES_tune-mips32r2-nf = "${TUNE_FEATURES_tune-mips-nf} mips32r2"
MIPSPKGSFX_VARIANT_tune-mips32r2-nf = "mips32r2"
PACKAGE_EXTRA_ARCHS_tune-mips32r2-nf = "mips-nf mips32-nf mips32r2-nf"

TUNE_FEATURES_tune-mips32r2el-nf = "${TUNE_FEATURES_tune-mipsel-nf} mips32r2"
MIPSPKGSFX_VARIANT_tune-mips32r2el-nf = "mips32r2el"
PACKAGE_EXTRA_ARCHS_tune-mips32r2el-nf = "mipsel-nf mips32el-nf mips32r2el-nf"
