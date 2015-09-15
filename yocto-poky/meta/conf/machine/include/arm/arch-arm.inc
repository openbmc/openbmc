
TUNEVALID[bigendian] = "Enable big-endian mode."

ARMPKGARCH = "${ARMPKGARCH_tune-${DEFAULTTUNE}}"
ARMPKGSFX_ENDIAN = "${@bb.utils.contains("TUNE_FEATURES", "bigendian", "b", "", d)}"
ARMPKGSFX_FPU ??= ""
ARMPKGSFX_DSP ??= ""
ARMPKGSFX_EABI ??= ""
ARMPKGSFX_THUMB ??= ""

TUNE_ARCH = "${@bb.utils.contains("TUNE_FEATURES", "bigendian", "armeb", "arm", d)}"
TUNE_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_THUMB}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}"

ABIEXTENSION = "eabi"

TARGET_FPU = "${@d.getVar('ARMPKGSFX_FPU', True).strip('-') or 'soft'}"

