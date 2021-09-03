#
# Defaults for ARMv8-m.main
#
DEFAULTTUNE ?= "armv8m-main"

require conf/machine/include/arm/arch-armv8m-base.inc
require conf/machine/include/arm/feature-arm-dsp.inc
require conf/machine/include/arm/feature-arm-neon.inc

TUNEVALID[armv8m-main] = "Enable instructions for ARMv8-m.main"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-main', ' -march=armv8-m.main', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-main', 'armv8m-main:', '', d)}"

TUNECONFLICTS[armv8m-main] = "armv4 armv5 armv6 armv7a"

AVAILTUNES                                      += "armv8m-main armv8m-maine armv8m-main-vfpv5spd16 armv8m-maine-vfpv5spd16"
ARMPKGARCH:tune-armv8m-main                      = "armv8m-main"
ARMPKGARCH:tune-armv8m-maine                     = "armv8m-main"
ARMPKGARCH:tune-armv8m-main-vfpv5spd16           = "armv8m-main"
ARMPKGARCH:tune-armv8m-maine-vfpv5spd16          = "armv8m-main"
TUNE_FEATURES:tune-armv8m-main                   = "armv8m-main"
TUNE_FEATURES:tune-armv8m-maine                  = "${TUNE_FEATURES:tune-armv8m-main} dsp"
TUNE_FEATURES:tune-armv8m-main-vfpv5spd16        = "${TUNE_FEATURES:tune-armv8m-main} vfpv5spd16"
TUNE_FEATURES:tune-armv8m-maine-vfpv5spd16       = "${TUNE_FEATURES:tune-armv8m-main-vfpv5spd16} dsp"
PACKAGE_EXTRA_ARCHS:tune-armv8m-main             = "armv8m-main"
PACKAGE_EXTRA_ARCHS:tune-armv8m-maine            = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-main} armv8m-maine"
PACKAGE_EXTRA_ARCHS:tune-armv8m-main-vfpv5spd16  = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-main} armv8m-main-fpv5-spd16"
PACKAGE_EXTRA_ARCHS:tune-armv8m-maine-vfpv5spd16 = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-main} armv8m-maine-fpv5-spd16"
