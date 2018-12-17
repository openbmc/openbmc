# m68k Architecture definitions

DEFAULTTUNE ?= "m68k"

# Architecture name
TUNE_ARCH = "m68k"

TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"
