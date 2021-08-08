DEFAULTTUNE ?= "armv5te"

require conf/machine/include/arm/arch-armv5-dsp.inc

TUNEVALID[arm926ejs] = "Enable arm926ejs specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm926ejs', ' -mcpu=arm926ej-s', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm926ejs', 'armv5:', '', d)}"

AVAILTUNES += "arm926ejs"
ARMPKGARCH:tune-arm926ejs = "arm926ejs"
# mcpu is used so don't use armv5te as we don't want march
TUNE_FEATURES:tune-arm926ejs = "arm thumb dsp arm926ejs"
PACKAGE_EXTRA_ARCHS:tune-arm926ejs = "${PACKAGE_EXTRA_ARCHS:tune-armv5te} arm926ejste arm926ejse"
