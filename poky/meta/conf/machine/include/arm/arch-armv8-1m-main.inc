#
#
# Defaults for ARMv8.1-M.main
#
DEFAULTTUNE ?= "armv8-1m-main"

TUNEVALID[armv8-1m-main] = "Enable instructions for ARMv8.1-m.main"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-1m-main', ' -march=armv8.1-m.main', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-1m-main', 'armv8-1m-main:', '', d)}"

TUNECONFLICTS[armv8-1m-main] = "armv4 armv5 armv6 armv7a"

require conf/machine/include/arm/arch-armv8m-main.inc

AVAILTUNES                            += "armv8-1m-main"
ARMPKGARCH:tune-armv8-1m-main          = "armv8-1m-main"
TUNE_FEATURES:tune-armv8-1m-main       = "armv8-1m-main"
PACKAGE_EXTRA_ARCHS:tune-armv8-1m-main = "armv8-1m-main"
