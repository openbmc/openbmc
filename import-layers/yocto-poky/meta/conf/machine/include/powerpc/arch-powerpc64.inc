DEFAULTTUNE ?= "powerpc64"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[m64] = "Power ELF64 standard ABI"
TUNECONFLICTS[m64] = "m32 nf"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'm64', ' -m64', '', d)}"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', [ 'm64' ], 'powerpc64', '', d)}"

# user mode qemu doesn't support ppc64
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = " ${@bb.utils.contains('TUNE_FEATURES', 'm64', 'qemu-usermode', '', d)}"

AVAILTUNES += "powerpc64"
TUNE_FEATURES_tune-powerpc64 = "m64 fpu-hard"
BASE_LIB_tune-powerpc64 = "lib64"
TUNE_PKGARCH_tune-powerpc64 = "powerpc64"
PACKAGE_EXTRA_ARCHS_tune-powerpc64 = "powerpc64"
