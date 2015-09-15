# SH Architecture definition

DEFAULTTUNE ?= "sh"

TUNE_ARCH = "${TUNE_ARCH_tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"

TUNEVALID[bigendian] = "Enabled big-endian mode."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "bigendian", " -mb", " -ml", d)}"
