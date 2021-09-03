DEFAULTTUNE ?= "mips64r2"

require conf/machine/include/mips/tune-mips64.inc

TUNEVALID[mips64r2] = "Enable mips64r2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mips64r2', ' -march=mips64r2', '', d)}"

# Base tunes
AVAILTUNES += "mips64r2 mips64r2el"

TUNE_FEATURES:tune-mips64r2 = "${TUNE_FEATURES:tune-mips64} mips64r2"
BASE_LIB:tune-mips64r2 = "lib64"
MIPSPKGSFX_VARIANT:tune-mips64r2 = "mips64r2"
PACKAGE_EXTRA_ARCHS:tune-mips64r2 = "mips64 mips64r2"

TUNE_FEATURES:tune-mips64r2el = "${TUNE_FEATURES:tune-mips64el} mips64r2"
BASE_LIB:tune-mips64r2el = "lib64"
MIPSPKGSFX_VARIANT:tune-mips64r2el = "mips64r2el"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el = "mips64el mips64r2el"

# MIPS 64r2 Soft Float
AVAILTUNES += "mips64r2-nf mips64r2el-nf"

TUNE_FEATURES:tune-mips64r2-nf = "${TUNE_FEATURES:tune-mips64-nf} mips64r2"
BASE_LIB:tune-mips64r2-nf = "lib64"
MIPSPKGSFX_VARIANT:tune-mips64r2-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2-nf = "mips64-nf mips64r2-nf"

TUNE_FEATURES:tune-mips64r2el-nf = "${TUNE_FEATURES:tune-mips64el-nf} mips64r2"
BASE_LIB:tune-mips64r2el-nf = "lib64"
MIPSPKGSFX_VARIANT:tune-mips64r2el-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el-nf = "mips64el-nf mips64r2el-nf"

# MIPS 64r2 n32
AVAILTUNES += "mips64r2-n32 mips64r2el-n32"

TUNE_FEATURES:tune-mips64r2-n32 = "${TUNE_FEATURES:tune-mips64-n32} mips64r2"
BASE_LIB:tune-mips64r2-n32 = "lib32"
MIPSPKGSFX_VARIANT:tune-mips64r2-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2-n32 = "mips64-n32 mips64r2-n32"

TUNE_FEATURES:tune-mips64r2el-n32 = "${TUNE_FEATURES:tune-mips64el-n32} mips64r2"
BASE_LIB:tune-mips64r2el-n32 = "lib32"
MIPSPKGSFX_VARIANT:tune-mips64r2el-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el-n32 = "mips64el-n32 mips64r2el-n32"

# MIPS 64r2 n32 and Soft Float
AVAILTUNES += "mips64r2-nf-n32 mips64r2el-nf-n32"

TUNE_FEATURES:tune-mips64r2-nf-n32 = "${TUNE_FEATURES:tune-mips64-nf-n32} mips64r2"
BASE_LIB:tune-mips64r2-nf-n32 = "lib32"
MIPSPKGSFX_VARIANT:tune-mips64r2-nf-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2-nf-n32 = "mips64-nf-n32 mips64r2-nf-n32"

TUNE_FEATURES:tune-mips64r2el-nf-n32 = "${TUNE_FEATURES:tune-mips64el-nf-n32} mips64r2"
BASE_LIB:tune-mips64r2el-nf-n32 = "lib32"
MIPSPKGSFX_VARIANT:tune-mips64r2el-nf-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el-nf-n32 = "mips64el-nf-n32 mips64r2el-nf-n32"

# MIPS 64r2 o32
AVAILTUNES += "mips64r2-o32 mips64r2el-o32"

TUNE_FEATURES:tune-mips64r2-o32 = "${TUNE_FEATURES:tune-mips64-o32} mips64r2"
BASE_LIB:tune-mips64r2-o32 = "lib"
MIPSPKGSFX_VARIANT:tune-mips64r2-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2-o32 = "mips mips64-o32 mips64r2-o32"

TUNE_FEATURES:tune-mips64r2el-o32 = "${TUNE_FEATURES:tune-mips64el-o32} mips64r2"
BASE_LIB:tune-mips64r2el-o32 = "lib"
MIPSPKGSFX_VARIANT:tune-mips64r2el-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el-o32 = "mipsel mips64el-o32 mips64r2el-o32"

# MIPS 64r2 o32 and Soft Float
AVAILTUNES += "mips64r2-nf-o32 mips64r2el-nf-o32"

TUNE_FEATURES:tune-mips64r2-nf-o32 = "${TUNE_FEATURES:tune-mips64-nf-o32} mips64r2"
BASE_LIB:tune-mips64r2-nf-o32 = "lib"
MIPSPKGSFX_VARIANT:tune-mips64r2-nf-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2-nf-o32 = "mips-nf mips64r2-nf-o32"

TUNE_FEATURES:tune-mips64r2el-nf-o32 = "${TUNE_FEATURES:tune-mips64el-nf-o32} mips64r2"
BASE_LIB:tune-mips64r2el-nf-o32 = "lib"
MIPSPKGSFX_VARIANT:tune-mips64r2el-nf-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS:tune-mips64r2el-nf-o32 = "mipsel-nf mips64r2el-nf-o32"
