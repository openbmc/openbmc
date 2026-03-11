# SH Architecture definition

DEFAULTTUNE ?= "sh"

TUNE_ARCH = "${TUNE_ARCH:tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH:tune-${DEFAULTTUNE}}"

TUNEVALID[bigendian] = "Enabled big-endian mode."
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', ' -mb', ' -ml', d)}"
