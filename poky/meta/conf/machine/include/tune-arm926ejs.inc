DEFAULTTUNE ?= "armv5te"

require conf/machine/include/arm/arch-armv5-dsp.inc

TUNEVALID[arm926ejs] = "Enable arm926ejs specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm926ejs', ' -mcpu=arm926ej-s', '', d)}"

AVAILTUNES += "arm926ejs"
ARMPKGARCH_tune-arm926ejs = "arm926ejs"
TUNE_FEATURES_tune-arm926ejs = "${TUNE_FEATURES_tune-armv5te} arm926ejs"
PACKAGE_EXTRA_ARCHS_tune-arm926ejs = "${PACKAGE_EXTRA_ARCHS_tune-armv5te} arm926ejste arm926ejse"
