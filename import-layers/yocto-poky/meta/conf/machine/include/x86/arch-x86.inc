#
# X86 Architecture definition (IA32, X86_64, and X32)
#

DEFAULTTUNE ?= "x86"
TARGET_FPU ?= ""
X86ARCH32 ?= "i586"
X86ARCH64 ?= "x86_64"

TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"

# ELF32 ABI
TUNEVALID[m32] = "IA32 ELF32 standard ABI"
TUNECONFLICTS[m32] = "m64 mx32"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'm32', '${X86ARCH32}', '' ,d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'm32', ' -m32', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'm32', 'x86:', '' ,d)}"

# x32 ABI
TUNEVALID[mx32] = "IA32e (x86_64) ELF32 standard ABI"
TUNECONFLICTS[mx32] = "m64 m32"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'mx32', '${X86ARCH64}', '' ,d)}"
ABIEXTENSION .= "${@bb.utils.contains('TUNE_FEATURES', 'mx32', 'x32', '' ,d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mx32', ' -mx32', '', d)}"
TUNE_LDARGS += "${@bb.utils.contains('TUNE_FEATURES', 'mx32', '-m elf32_x86_64', '', d)}"
TUNE_ASARGS += "${@bb.utils.contains('TUNE_FEATURES', 'mx32', '-x32', '', d)}"
# user mode qemu doesn't support x32
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = " ${@bb.utils.contains('TUNE_FEATURES', 'mx32', 'qemu-usermode', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'mx32', 'x86-x32:', '' ,d)}"

# ELF64 ABI
TUNEVALID[m64] = "IA32e (x86_64) ELF64 standard ABI"
TUNECONFLICTS[m64] = "m32 mx32"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'm64', '${X86ARCH64}', '' ,d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'm64', ' -m64', '', d)}"

# Default Tune configurations
AVAILTUNES += "x86"
TUNE_FEATURES_tune-x86 = "m32"
BASE_LIB_tune-x86 = "lib"
TUNE_PKGARCH_tune-x86 = "x86"
PACKAGE_EXTRA_ARCHS_tune-x86 = "${TUNE_PKGARCH_tune-x86}"

AVAILTUNES += "x86-64"
TUNE_FEATURES_tune-x86-64 = "m64"
BASE_LIB_tune-x86-64 = "lib64"
TUNE_PKGARCH_tune-x86-64 = "x86_64"
PACKAGE_EXTRA_ARCHS_tune-x86-64 = "${TUNE_PKGARCH_tune-x86-64}"

AVAILTUNES += "x86-64-x32"
TUNE_FEATURES_tune-x86-64-x32 = "mx32"
BASE_LIB_tune-x86-64-x32 = "libx32"
TUNE_PKGARCH_tune-x86-64-x32 = "x86_64_x32"
PACKAGE_EXTRA_ARCHS_tune-x86-64-x32 = "${TUNE_PKGARCH_tune-x86-64-x32}"
