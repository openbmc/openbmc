DEFAULTTUNE ?= "aarch64"

require conf/machine/include/arm/arch-armv7ve.inc

TUNEVALID[aarch64] = "Enable instructions for aarch64"
TUNECONFLICTS[aarch64] = "armv4 armv5 armv6 armv7 armv7a"

MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', 'aarch64:', '', d)}"

# Little Endian base configs
AVAILTUNES += "aarch64 aarch64_be"
ARMPKGARCH:tune-aarch64 ?= "aarch64"
ARMPKGARCH:tune-aarch64_be ?= "aarch64"
TUNE_FEATURES:tune-aarch64 = "aarch64"
TUNE_FEATURES:tune-aarch64_be = "${TUNE_FEATURES:tune-aarch64} bigendian"
TUNE_PKGARCH_64:tune-aarch64 = "aarch64"
TUNE_PKGARCH_64:tune-aarch64_be = "aarch64_be"
BASE_LIB:tune-aarch64 = "lib64"
BASE_LIB:tune-aarch64_be = "lib64"

PACKAGE_EXTRA_ARCHS:tune-aarch64 = "aarch64"
PACKAGE_EXTRA_ARCHS:tune-aarch64_be = "aarch64_be"

ARMPKGSFX_ENDIAN_64 = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '_be', '', d)}"
TUNE_ARCH_64 = "aarch64${ARMPKGSFX_ENDIAN_64}"
TUNE_PKGARCH_64 = "${ARMPKGARCH}${ARMPKGSFX_ENDIAN_64}"
ABIEXTENSION_64 = ""
TARGET_FPU_64 = ""

# Duplicated from arch-arm.inc
TUNE_ARCH_32 = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'armeb', 'arm', d)}"
TUNE_PKGARCH_32 = "${ARMPKGARCH}${ARMPKGSFX_THUMB}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}"
ABIEXTENSION_32 = "eabi"
TARGET_FPU_32 = "${@d.getVar('TUNE_CCARGS_MFLOAT') or 'soft'}"

TUNE_ARCH = "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '${TUNE_ARCH_64}', '${TUNE_ARCH_32}', d)}"
TUNE_PKGARCH = "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '${TUNE_PKGARCH_64}', '${TUNE_PKGARCH_32}', d)}"
ABIEXTENSION = "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '${ABIEXTENSION_64}', '${ABIEXTENSION_32}', d)}"
TARGET_FPU = "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '${TARGET_FPU_64}', '${TARGET_FPU_32}', d)}"

# Emit branch protection (PAC/BTI) instructions.  On hardware that doesn't
# support these they're meaningless NOP instructions, so there's very little
# reason not to.
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', ' -mbranch-protection=standard', '', d)}"
