DEFAULTTUNE ?= "armv5"

TUNEVALID[armv5] = "Enable instructions for ARMv5"
TUNECONFLICTS[armv5] = "armv4"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'armv5', ' -march=armv5t${ARMPKGSFX_DSP}', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv5', 'armv5:', '', d)}"

require conf/machine/include/arm/arch-armv4.inc
require conf/machine/include/arm/feature-arm-vfp.inc

# Little Endian
AVAILTUNES += "armv5 armv5t"
ARMPKGARCH_tune-armv5  ?= "armv5"
ARMPKGARCH_tune-armv5t ?= "armv5"
TUNE_FEATURES_tune-armv5  = "arm armv5"
TUNE_FEATURES_tune-armv5t = "${TUNE_FEATURES_tune-armv5} thumb"
PACKAGE_EXTRA_ARCHS_tune-armv5  = "${PACKAGE_EXTRA_ARCHS_tune-armv4} armv5"
PACKAGE_EXTRA_ARCHS_tune-armv5t = "${PACKAGE_EXTRA_ARCHS_tune-armv4t} armv5 armv5t"

# Little Endian + VFP/DSP
AVAILTUNES += "armv5-vfp armv5t-vfp armv5hf-vfp armv5thf-vfp"
ARMPKGARCH_tune-armv5-vfp    ?= "armv5"
ARMPKGARCH_tune-armv5t-vfp   ?= "armv5"
ARMPKGARCH_tune-armv5hf-vfp  ?= "armv5"
ARMPKGARCH_tune-armv5thf-vfp ?= "armv5"
TUNE_FEATURES_tune-armv5-vfp    = "${TUNE_FEATURES_tune-armv5} vfp"
TUNE_FEATURES_tune-armv5t-vfp   = "${TUNE_FEATURES_tune-armv5t} vfp"
TUNE_FEATURES_tune-armv5hf-vfp  = "${TUNE_FEATURES_tune-armv5-vfp} callconvention-hard"
TUNE_FEATURES_tune-armv5thf-vfp = "${TUNE_FEATURES_tune-armv5t-vfp} callconvention-hard"
PACKAGE_EXTRA_ARCHS_tune-armv5-vfp     = "${PACKAGE_EXTRA_ARCHS_tune-armv5} armv5-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5t-vfp    = "${PACKAGE_EXTRA_ARCHS_tune-armv5t} armv5-vfp armv5t-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5hf-vfp   = "armv5hf-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5thf-vfp  = "armv5hf-vfp armv5thf-vfp"

# Big Endian
AVAILTUNES += "armv5b armv5tb"
ARMPKGARCH_tune-armv5b  ?= "armv5"
ARMPKGARCH_tune-armv5tb ?= "armv5"
TUNE_FEATURES_tune-armv5b  = "${TUNE_FEATURES_tune-armv5} bigendian"
TUNE_FEATURES_tune-armv5tb = "${TUNE_FEATURES_tune-armv5t} bigendian"
PACKAGE_EXTRA_ARCHS_tune-armv5b  = "${PACKAGE_EXTRA_ARCHS_tune-armv4b} armv5b"
PACKAGE_EXTRA_ARCHS_tune-armv5tb = "${PACKAGE_EXTRA_ARCHS_tune-armv4tb} armv5b armv5tb"

# Big Endian + VFP/DSP
AVAILTUNES += "armv5b-vfp armv5tb-vfp armv5hfb-vfp armv5thfb-vfp"
ARMPKGARCH_tune-armv5b-vfp    ?= "armv5"
ARMPKGARCH_tune-armv5tb-vfp   ?= "armv5"
ARMPKGARCH_tune-armv5hfb-vfp  ?= "armv5"
ARMPKGARCH_tune-armv5thfb-vfp ?= "armv5"
TUNE_FEATURES_tune-armv5b-vfp    = "${TUNE_FEATURES_tune-armv5-vfp} bigendian"
TUNE_FEATURES_tune-armv5tb-vfp   = "${TUNE_FEATURES_tune-armv5t-vfp} bigendian"
TUNE_FEATURES_tune-armv5hfb-vfp  = "${TUNE_FEATURES_tune-armv5hf-vfp} bigendian"
TUNE_FEATURES_tune-armv5thfb-vfp = "${TUNE_FEATURES_tune-armv5thf-vfp} bigendian"
PACKAGE_EXTRA_ARCHS_tune-armv5b-vfp    = "${PACKAGE_EXTRA_ARCHS_tune-armv5b} armv5b-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5tb-vfp   = "${PACKAGE_EXTRA_ARCHS_tune-armv5tb} armv5b-vfp armv5tb-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5hfb-vfp  = "armv5hfb-vfp"
PACKAGE_EXTRA_ARCHS_tune-armv5thfb-vfp = "armv5hfb-vfp armv5thfb-vfp"
