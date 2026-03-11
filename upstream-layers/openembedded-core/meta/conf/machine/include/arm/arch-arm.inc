
TUNEVALID[bigendian] = "Enable big-endian mode."

ARMPKGARCH = "${ARMPKGARCH:tune-${DEFAULTTUNE}}"
ARMPKGSFX_ENDIAN = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'b', '', d)}"
ARMPKGSFX_FPU ??= ""
ARMPKGSFX_DSP ??= ""
ARMPKGSFX_EABI ??= ""
ARMPKGSFX_THUMB ??= ""

TUNE_ARCH = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'armeb', 'arm', d)}"
TUNE_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_THUMB}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}"

ABIEXTENSION = "eabi"

TARGET_FPU = "${@d.getVar('TUNE_CCARGS_MFLOAT') or 'soft'}"

# Some -march settings need a +X option passed in.  Since we cannot guarantee that any specified TUNE_CCARGS option is set in any order, we must hard code the order here to allow for it.
TUNE_CCARGS_MARCH_OPTS ??= ""
TUNE_CCARGS .= "${TUNE_CCARGS_MARCH}${TUNE_CCARGS_MARCH_OPTS}"
