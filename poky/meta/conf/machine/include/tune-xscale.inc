DEFAULTTUNE ?= "armv5te"

require conf/machine/include/arm/arch-armv5-dsp.inc

TUNEVALID[xscale] = "Enable PXA255/PXA26x Xscale specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'xscale', ' -mcpu=xscale', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'xscale', 'armv5:', '', d)}"

AVAILTUNES += "xscale"
ARMPKGARCH_tune-xscale = "xscale"
# mcpu is used so don't use armv5te as we don't want march
TUNE_FEATURES_tune-xscale = "arm thumb dsp xscale"
PACKAGE_EXTRA_ARCHS_tune-xscale = "${PACKAGE_EXTRA_ARCHS_tune-armv5te} xscale xscalet xscalee xscalete"

AVAILTUNES += "xscale-be"
ARMPKGARCH_tune-xscale-be = "xscale"
# mcpu is used so don't use armv5te as we don't want march
TUNE_FEATURES_tune-xscale-be = "${TUNE_FEATURES_tune-xscale} bigendian"
PACKAGE_EXTRA_ARCHS_tune-xscale-be = "${PACKAGE_EXTRA_ARCHS_tune-armv5teb} xscaleb xscaletb xscaleeb xscaleteb"
