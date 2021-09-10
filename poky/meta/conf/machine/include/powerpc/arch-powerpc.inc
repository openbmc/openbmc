# Power Architecture definition
# Four defined ABIs, all combinations of:
# *) Hard/Soft Floating Point
# *) 32-bit/64-bit

DEFAULTTUNE ?= "powerpc"

TUNE_PKGARCH = "${TUNE_PKGARCH:tune-${DEFAULTTUNE}}"
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

TUNE_FEATURES:tune-powerpc-nf = "m32 fpu-soft bigendian"
BASE_LIB:tune-powerpc-nf = "lib"
TUNE_PKGARCH:tune-powerpc-nf = "powerpc-nf"
PACKAGE_EXTRA_ARCHS:tune-powerpc-nf = "powerpc-nf"

TUNE_FEATURES:tune-powerpc = "m32 fpu-hard bigendian"
BASE_LIB:tune-powerpc = "lib"
TUNE_PKGARCH:tune-powerpc = "powerpc"
PACKAGE_EXTRA_ARCHS:tune-powerpc = "powerpc"

TUNE_FEATURES:tune-powerpcle-nf = "m32 fpu-soft"
BASE_LIB:tune-powerpcle-nf = "lib"
TUNE_PKGARCH:tune-powerpcle-nf = "powerpcle-nf"
PACKAGE_EXTRA_ARCHS:tune-powerpcle-nf = "powerpcle-nf"

TUNE_FEATURES:tune-powerpcle = "m32 fpu-hard"
BASE_LIB:tune-powerpcle = "lib"
TUNE_PKGARCH:tune-powerpcle = "powerpcle"
PACKAGE_EXTRA_ARCHS:tune-powerpcle = "powerpcle"
