#
# Tune Settings for Cortex-M33
#
DEFAULTTUNE ?= "cortexm33"

TUNEVALID[cortexm33] = "Enable Cortex-M33 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm33', ' -mcpu=cortex-m33', '', d)}"

require conf/machine/include/arm/arch-armv8m-main.inc

# GCC thnks that DSP and VFP are required, but Arm docs say it is
# optional.  So forcing below so that compiling works, but this should
# be fixed in GCC
AVAILTUNES                          += "cortexm33"
ARMPKGARCH:tune-cortexm33            = "cortexm33"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexm33         = "vfpv5spd16 dsp cortexm33"
PACKAGE_EXTRA_ARCHS:tune-cortexm33   = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-maine-vfpv5spd16} cortexm33e-fpv5-spd16"
