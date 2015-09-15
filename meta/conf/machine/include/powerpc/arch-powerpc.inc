# Power Architecture definition
# Four defined ABIs, all combinations of:
# *) Hard/Soft Floating Point
# *) 32-bit/64-bit

DEFAULTTUNE ?= "powerpc"

TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"
ABIEXTENSION ?= ""

TUNEVALID[m32] = "Power ELF32 standard ABI"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "m32", " -m32", "", d)}"
TUNE_ARCH .= "${@bb.utils.contains("TUNE_FEATURES", "m32", "powerpc", "", d)}"

TUNEVALID[fpu-hard] = "Use hardware FPU."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "fpu-hard", " -mhard-float", "", d)}"

TUNEVALID[fpu-soft] = "Use software FPU."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "fpu-soft", " -msoft-float", "", d)}"
TARGET_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "fpu-soft", "soft", "", d)}"

TUNEVALID[altivec] = "Altivec"

# Basic tune definitions
AVAILTUNES += "powerpc powerpc-nf" 
TUNE_FEATURES_tune-powerpc-nf = "m32 fpu-soft"
BASE_LIB_tune-powerpc-nf = "lib"
TUNE_PKGARCH_tune-powerpc-nf = "powerpc-nf"
PACKAGE_EXTRA_ARCHS_tune-powerpc-nf = "powerpc-nf"

TUNE_FEATURES_tune-powerpc = "m32 fpu-hard"
BASE_LIB_tune-powerpc = "lib"
TUNE_PKGARCH_tune-powerpc = "powerpc"
PACKAGE_EXTRA_ARCHS_tune-powerpc = "powerpc"
