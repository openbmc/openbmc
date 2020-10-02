# Configurations for the Intel PXA27x Appications Processor Family. 
# Please use tune-xscale for PXA255/PXA26x based processors.

DEFAULTTUNE ?= "iwmmxt"

require conf/machine/include/arm/arch-armv5-dsp.inc

TUNEVALID[iwmmxt] = "Enable Intel PXA27x specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'iwmmxt', ' -mcpu=iwmmxt', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'iwmmxt', 'armv5:', '', d)}"

AVAILTUNES += "iwmmxt"
ARMPKGARCH_tune-iwmmxt = "iwmmxt"
TUNE_FEATURES_tune-iwmmxt = "thumb iwmmxt"
PACKAGE_EXTRA_ARCHS_tune-iwmmxt = "${PACKAGE_EXTRA_ARCHS_tune-armv5te} iwmmxt iwmmxtt"
