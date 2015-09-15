DEFAULTTUNE ?= "armv5te"

require conf/machine/include/arm/arch-armv5-dsp.inc

TUNEVALID[xscale] = "Enable PXA255/PXA26x Xscale specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "xscale", " -mtune=xscale", "", d)}"

AVAILTUNES += "xscale"
ARMPKGARCH_tune-xscale = "xscale"
TUNE_FEATURES_tune-xscale = "${TUNE_FEATURES_tune-armv5te} xscale"
PACKAGE_EXTRA_ARCHS_tune-xscale = "${PACKAGE_EXTRA_ARCHS_tune-armv5te} xscale xscalet xscalee xscalete"

AVAILTUNES += "xscale-be"
ARMPKGARCH_tune-xscale-be = "xscale"
TUNE_FEATURES_tune-xscale-be = "${TUNE_FEATURES_tune-armv5teb} xscale bigendian"
PACKAGE_EXTRA_ARCHS_tune-xscale-be = "${PACKAGE_EXTRA_ARCHS_tune-armv5teb} xscaleb xscaletb xscaleeb xscaleteb"
