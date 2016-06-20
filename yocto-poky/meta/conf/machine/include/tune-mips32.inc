DEFAULTTUNE ?= "mips32"

require conf/machine/include/mips/arch-mips.inc

TUNEVALID[mips32] = "Enable mips32 specific processor optimizations"
TUNECONFLICTS[mips32] = "n64 n32"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mips32', ' -march=mips32', '', d)}"

AVAILTUNES += "mips32 mips32el mips32-nf mips32el-nf"

TUNE_FEATURES_tune-mips32 = "${TUNE_FEATURES_tune-mips} mips32"
MIPSPKGSFX_VARIANT_tune-mips32 = "mips32"
PACKAGE_EXTRA_ARCHS_tune-mips32 = "mips mips32"

TUNE_FEATURES_tune-mips32el = "${TUNE_FEATURES_tune-mipsel} mips32"
MIPSPKGSFX_VARIANT_tune-mips32el = "mips32el"
PACKAGE_EXTRA_ARCHS_tune-mips32el = "mipsel mips32el"

TUNE_FEATURES_tune-mips32-nf = "${TUNE_FEATURES_tune-mips-nf} mips32"
MIPSPKGSFX_VARIANT_tune-mips32-nf = "mips32"
PACKAGE_EXTRA_ARCHS_tune-mips32-nf = "mips-nf mips32-nf"

TUNE_FEATURES_tune-mips32el-nf = "${TUNE_FEATURES_tune-mipsel-nf} mips32"
MIPSPKGSFX_VARIANT_tune-mips32el-nf = "mips32el"
PACKAGE_EXTRA_ARCHS_tune-mips32el-nf = "mipsel-nf mips32el-nf"
