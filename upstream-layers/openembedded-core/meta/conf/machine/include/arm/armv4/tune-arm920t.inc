DEFAULTTUNE ?= "arm920t"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[arm920t] = "Enable arm920t specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm920t', ' -mcpu=arm920t', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm920t', 'armv4:', '', d)}"

AVAILTUNES += "arm920t"
ARMPKGARCH:tune-arm920t = "arm920t"
# mcpu is used so don't use armv4t as we don't want march
TUNE_FEATURES:tune-arm920t = "arm thumb arm920t"
PACKAGE_EXTRA_ARCHS:tune-arm920t = "${PACKAGE_EXTRA_ARCHS:tune-armv4t} arm920t arm920tt"
