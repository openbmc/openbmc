#
# Defaults for ARMv8-m.main
#
DEFAULTTUNE ?= "armv8m-main"

require conf/machine/include/arm/arch-armv8m-base.inc

TUNEVALID[armv8m-main] = "Enable instructions for ARMv8-m.main"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-main', ' -march=armv8-m.main${MARCH_DSP}${MARCH_FPU}', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-main', 'armv8m-main:', '', d)}"

TUNECONFLICTS[armv8m-main] = "armv4 armv5 armv6 armv7a"

# FIXME - below taken from meta/conf/machine/include/arm/arch-armv5-dsp.inc and should be put into something more generic
TUNEVALID[dsp] = "ARM DSP functionality"
ARMPKGSFX_DSP = "${@bb.utils.contains('TUNE_FEATURES', [ 'dsp' ], 'e', '', d)}"
MARCH_DSP = "${@bb.utils.contains('TUNE_FEATURES', [ 'dsp' ], '+dsp', '+nodsp', d)}"

# FIXME - Below belongs in meta/conf/machine/include/arm/feature-arm-neon.inc
TUNEVALID[vfpv5spd16] = "Enable Vector Floating Point Version 5, Single Precision. with 16 registers (fpv5-sp-d16) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv5spd16', 'fpv5-sp-d16', '', d)}"
MARCH_FPU = "${@bb.utils.contains('TUNE_FEATURES', [ 'vfpv5spd16' ], '+fp', '+nofp', d)}"

AVAILTUNES                                      += "armv8m-main armv8m-mainearmv8m-main-vfpv5spd16 armv8m-maine-vfpv5spd16"
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
