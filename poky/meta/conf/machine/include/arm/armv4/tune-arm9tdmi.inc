DEFAULTTUNE ?= "arm9tdmi"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[arm9tdmi] = "Enable arm9tdmi specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm9tdmi', ' -mcpu=arm9tdmi', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm9tdmi', 'armv4:', '', d)}"

AVAILTUNES += "arm9tdmi"
ARMPKGARCH:tune-arm9tdmi = "arm9tdmi"
# mcpu is used so don't use armv4t as we don't want march
TUNE_FEATURES:tune-arm9tdmi = "arm thumb arm9tdmi"
PACKAGE_EXTRA_ARCHS:tune-arm9tdmi = "${PACKAGE_EXTRA_ARCHS:tune-armv4t} arm9tdmi arm9tdmit"
