#
# Tune Settings for Cortex-M35P
#
DEFAULTTUNE ?= "cortexm35p"

TUNEVALID[cortexm35p] = "Enable Cortex-M35p specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm35p', ' -mcpu=cortex-m35p', '', d)}"

require conf/machine/include/arm/arch-armv8m-main.inc

# GCC thnks that DSP and VFP are required, but Arm docs say it is
# optional.  So forcing below so that compiling works, but this should
# be fixed in GCC
AVAILTUNES                          += "cortexm35p"
ARMPKGARCH:tune-cortexm35p           = "cortexm35p"
TUNE_FEATURES:tune-cortexm35p        = "${TUNE_FEATURES:tune-armv8m-maine-vfpv5spd16} cortexm35p"
PACKAGE_EXTRA_ARCHS:tune-cortexm35p  = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-maine-vfpv5spd16} cortexm35pe-fpv5-spd16"
