# Power Architecture definition
# Four defined ABIs, all combinations of:
# *) Hard/Soft Floating Point
# *) 32-bit/64-bit

DEFAULTTUNE ?= "powerpc"

TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"
ABIEXTENSION ?= ""

# Endian
TUNEVALID[bigendian] = "Use PowerPC Big Endian"

TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "bigendian", " -mbig-endian", " -mlittle-endian", d)}"

TUNEVALID[m32] = "Power ELF32 standard ABI"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'm32', ' -m32', '', d)}"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'm32', 'powerpc${ENDIAN_SFX}', '', d)}"
ENDIAN_SFX = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'le', d)}"

TUNEVALID[fpu-hard] = "Use hardware FPU."
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard', ' -mhard-float', '', d)}"

TUNEVALID[fpu-soft] = "Use software FPU."
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-soft', ' -msoft-float', '', d)}"
TARGET_FPU .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-soft', 'soft', '', d)}"

TUNEVALID[altivec] = "Altivec"

# Basic tune definitions
AVAILTUNES += "powerpc powerpc-nf powerpcle powerpcle-nf" 

TUNE_FEATURES_tune-powerpc-nf = "m32 fpu-soft bigendian"
BASE_LIB_tune-powerpc-nf = "lib"
TUNE_PKGARCH_tune-powerpc-nf = "powerpc-nf"
PACKAGE_EXTRA_ARCHS_tune-powerpc-nf = "powerpc-nf"

TUNE_FEATURES_tune-powerpc = "m32 fpu-hard bigendian"
BASE_LIB_tune-powerpc = "lib"
TUNE_PKGARCH_tune-powerpc = "powerpc"
PACKAGE_EXTRA_ARCHS_tune-powerpc = "powerpc"

TUNE_FEATURES_tune-powerpcle-nf = "m32 fpu-soft"
BASE_LIB_tune-powerpcle-nf = "lib"
TUNE_PKGARCH_tune-powerpcle-nf = "powerpcle-nf"
PACKAGE_EXTRA_ARCHS_tune-powerpcle-nf = "powerpcle-nf"

TUNE_FEATURES_tune-powerpcle = "m32 fpu-hard"
BASE_LIB_tune-powerpcle = "lib"
TUNE_PKGARCH_tune-powerpcle = "powerpcle"
PACKAGE_EXTRA_ARCHS_tune-powerpcle = "powerpcle"
