# MIPS Architecture definition
# 12 defined ABIs, all combinations of:
# *) Big/Little Endian
# *) Hardware/Software Floating Point
# *) o32, n32, n64 ABI

DEFAULTTUNE ?= "mips"

# Endianess
TUNEVALID[bigendian] = "Enable big-endian mode"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', ' -meb', ' -mel', d)}"

# ABI flags
TUNEVALID[o32] = "MIPS o32 ABI"
TUNECONFLICTS[o32] = "n32 n64"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'o32', ' -mabi=32', '', d)}"

TUNEVALID[n32] = "MIPS64 n32 ABI"
TUNECONFLICTS[n32] = "o32 n64"
ABIEXTENSION .= "${@bb.utils.filter('TUNE_FEATURES', 'n32' ,d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'n32', ' -mabi=n32', '', d)}"

# user mode qemu doesn't support mips64 n32: "Invalid ELF image for this architecture"
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = " ${@bb.utils.contains('TUNE_FEATURES', 'n32', 'qemu-usermode', '', d)}"

TUNEVALID[n64] = "MIPS64 n64 ABI"
TUNECONFLICTS[n64] = "o32 n32"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'n64', ' -mabi=64', '', d)}"

# Floating point
TUNEVALID[fpu-hard] = "Use hardware FPU"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard', ' -mhard-float', ' -msoft-float', d)}"
TARGET_FPU = "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard', '', 'soft', d)}"

# MIPS r6 ABI
TUNEVALID[r6] = "Use r6"
TUNEVALID[mipsisa64r6] = "Use 64r6"
TUNEVALID[mipsisa32r6] = "Use 32r6"

# Package naming
MIPSPKGSFX_ENDIAN = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'el', d)}"
MIPSPKGSFX_ENDIAN2 = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'eb', 'el', d)}"
MIPSPKGSFX_BYTE = "${@bb.utils.contains('TUNE_FEATURES', 'n64' , '64', '', d)}"
MIPSPKGSFX_BYTE .= "${@bb.utils.contains('TUNE_FEATURES', 'n32' , '64', '', d)}"
MIPSPKGSFX_FPU = "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard' , '', '-nf', d)}"
MIPSPKGSFX_ABI = "${@bb.utils.contains('TUNE_FEATURES', 'n32', '-n32', '', d)}"
MIPSPKGSFX_R6 = "${@bb.utils.filter('TUNE_FEATURES', 'r6', d)}"
MIPSPKGSFX_64R6 = "${@bb.utils.contains('TUNE_FEATURES', 'mipsisa64r6', 'isa', '', d)}"
MIPSPKGSFX_32R6 = "${@bb.utils.contains('TUNE_FEATURES', 'mipsisa32r6', 'isa32', '', d)}"

TUNE_ARCH = "mips${MIPSPKGSFX_32R6}${MIPSPKGSFX_64R6}${MIPSPKGSFX_BYTE}${MIPSPKGSFX_R6}${MIPSPKGSFX_ENDIAN}"
TUNE_PKGARCH = "${MIPSPKGSFX_VARIANT_tune-${DEFAULTTUNE}}${MIPSPKGSFX_FPU}${MIPSPKGSFX_ABI}"

# Various Global Machine Overrides
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'n64', 'mipsarchn64${MIPSPKGSFX_ENDIAN2}:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'o32', 'mipsarcho32${MIPSPKGSFX_ENDIAN2}:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'n32', 'mipsarchn32${MIPSPKGSFX_ENDIAN2}:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'n64', 'mipsarchn64:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'o32', 'mipsarcho32:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'n32', 'mipsarchn32:', '' ,d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'r6', 'mipsarchr6:', '' ,d)}"
MACHINEOVERRIDES =. "mipsarch${MIPSPKGSFX_ENDIAN2}:"
MACHINEOVERRIDES =. "mipsarch:"

# Base tunes
AVAILTUNES += "mips mips64-n32 mips64 mipsel mips64el-n32 mips64el mips-nf mips64-nf-n32 mips64-nf mipsel-nf mips64el-nf-n32 mips64el-nf"
TUNE_FEATURES_tune-mips = "o32 bigendian fpu-hard"
BASE_LIB_tune-mips = "lib"
MIPSPKGSFX_VARIANT_tune-mips = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips = "mips"

TUNE_FEATURES_tune-mips64-n32 = "n32 bigendian fpu-hard"
BASE_LIB_tune-mips64-n32 = "lib32"
MIPSPKGSFX_VARIANT_tune-mips64-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64-n32 = "mips64-n32"

TUNE_FEATURES_tune-mips64 = "n64 bigendian fpu-hard"
BASE_LIB_tune-mips64 = "lib64"
MIPSPKGSFX_VARIANT_tune-mips64 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64 = "mips64"

TUNE_FEATURES_tune-mipsel = "o32 fpu-hard"
BASE_LIB_tune-mipsel = "lib"
MIPSPKGSFX_VARIANT_tune-mipsel = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mipsel = "mipsel"

TUNE_FEATURES_tune-mips64el-n32 = "n32 fpu-hard"
BASE_LIB_tune-mips64el-n32 = "lib32"
MIPSPKGSFX_VARIANT_tune-mips64el-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el-n32 = "mips64el-n32"

TUNE_FEATURES_tune-mips64el = "n64 fpu-hard"
BASE_LIB_tune-mips64el = "lib64"
MIPSPKGSFX_VARIANT_tune-mips64el = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el = "mips64el"

TUNE_FEATURES_tune-mips-nf = "o32 bigendian"
BASE_LIB_tune-mips-nf = "lib"
MIPSPKGSFX_VARIANT_tune-mips-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips-nf = "mips-nf"

TUNE_FEATURES_tune-mips64-nf-n32 = "n32 bigendian"
BASE_LIB_tune-mips64-nf-n32 = "lib32"
MIPSPKGSFX_VARIANT_tune-mips64-nf-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64-nf-n32 = "mips64-nf-n32"

TUNE_FEATURES_tune-mips64-nf = "n64 bigendian"
BASE_LIB_tune-mips64-nf = "lib64"
MIPSPKGSFX_VARIANT_tune-mips64-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64-nf = "mips64-nf"

TUNE_FEATURES_tune-mipsel-nf = "o32"
BASE_LIB_tune-mipsel-nf = "lib"
MIPSPKGSFX_VARIANT_tune-mipsel-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mipsel-nf = "mipsel-nf"

TUNE_FEATURES_tune-mips64el-nf-n32 = "n32"
BASE_LIB_tune-mips64el-nf-n32 = "lib32"
MIPSPKGSFX_VARIANT_tune-mips64el-nf-n32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el-nf-n32 = "mips64el-nf-n32"

TUNE_FEATURES_tune-mips64el-nf = "n64"
BASE_LIB_tune-mips64el-nf = "lib64"
MIPSPKGSFX_VARIANT_tune-mips64el-nf = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el-nf = "mips64el-nf"

# MIPS 64 + o32
AVAILTUNES += "mips64-o32 mips64el-o32"

TUNE_FEATURES_tune-mips64-o32 = "o32 bigendian fpu-hard"
BASE_LIB_tune-mips64-o32 = "lib"
MIPSPKGSFX_VARIANT_tune-mips64-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64-o32 = "mips mips64-o32"

TUNE_FEATURES_tune-mips64el-o32 = "o32 fpu-hard"
BASE_LIB_tune-mips64el-o32 = "lib"
MIPSPKGSFX_VARIANT_tune-mips64el-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el-o32 = "mipsel mips64el-o32 mips64el-o32"

# MIPS 64 o32 and Soft Float
AVAILTUNES += "mips64-nf-o32 mips64el-nf-o32"

TUNE_FEATURES_tune-mips64-nf-o32 = "o32 bigendian"
BASE_LIB_tune-mips64-nf-o32 = "lib"
MIPSPKGSFX_VARIANT_tune-mips64-nf-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64-nf-o32 = "mips-nf mips64-nf-o32"

TUNE_FEATURES_tune-mips64el-nf-o32 = "o32"
BASE_LIB_tune-mips64el-nf-o32 = "lib"
MIPSPKGSFX_VARIANT_tune-mips64el-nf-o32 = "${TUNE_ARCH}"
PACKAGE_EXTRA_ARCHS_tune-mips64el-nf-o32 = "mipsel-nf mips64el-nf-o32"
