# LoongArch Architecture definition

DEFAULTTUNE ?= "loongarch64"

TUNE_ARCH = "${TUNE_ARCH:tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH:tune-${DEFAULTTUNE}}"
TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'loongarch64', ' -march=loongarch64 -mabi=lp64d', ' ', d)}"
