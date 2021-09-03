DEFAULTTUNE ?= "cortexa9thf-neon"

require conf/machine/include/arm/arch-armv7a.inc

TUNEVALID[cortexa9] = "Enable Cortex-A9 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa9', ' -mcpu=cortex-a9', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'cortexa9', 'armv7a:', '', d)}"

# Little Endian base configs
AVAILTUNES += "cortexa9 cortexa9t cortexa9-neon cortexa9t-neon"
ARMPKGARCH:tune-cortexa9             = "cortexa9"
ARMPKGARCH:tune-cortexa9t            = "cortexa9"
ARMPKGARCH:tune-cortexa9-neon        = "cortexa9"
ARMPKGARCH:tune-cortexa9t-neon       = "cortexa9"
# mcpu is used so don't use armv7a as we don't want march
TUNE_FEATURES:tune-cortexa9             = "arm vfp cortexa9"
TUNE_FEATURES:tune-cortexa9t            = "${TUNE_FEATURES:tune-cortexa9} thumb"
TUNE_FEATURES:tune-cortexa9-neon        = "${TUNE_FEATURES:tune-cortexa9} neon"
TUNE_FEATURES:tune-cortexa9t-neon       = "${TUNE_FEATURES:tune-cortexa9-neon} thumb"
PACKAGE_EXTRA_ARCHS:tune-cortexa9             = "${PACKAGE_EXTRA_ARCHS:tune-armv7a} cortexa9-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa9t            = "${PACKAGE_EXTRA_ARCHS:tune-armv7at} cortexa9-vfp cortexa9t2-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa9-neon        = "${PACKAGE_EXTRA_ARCHS:tune-armv7a-neon} cortexa9-vfp cortexa9-neon"
PACKAGE_EXTRA_ARCHS:tune-cortexa9t-neon       = "${PACKAGE_EXTRA_ARCHS:tune-armv7at-neon} cortexa9-vfp cortexa9-neon cortexa9t2-vfp cortexa9t2-neon"

# HF Tunes
AVAILTUNES += "cortexa9hf cortexa9thf cortexa9hf-neon cortexa9thf-neon"
ARMPKGARCH:tune-cortexa9hf             = "cortexa9"
ARMPKGARCH:tune-cortexa9thf            = "cortexa9"
ARMPKGARCH:tune-cortexa9hf-neon        = "cortexa9"
ARMPKGARCH:tune-cortexa9thf-neon       = "cortexa9"
# mcpu is used so don't use armv7a as we don't want march
TUNE_FEATURES:tune-cortexa9hf             = "${TUNE_FEATURES:tune-cortexa9} callconvention-hard"
TUNE_FEATURES:tune-cortexa9thf            = "${TUNE_FEATURES:tune-cortexa9t} callconvention-hard"
TUNE_FEATURES:tune-cortexa9hf-neon        = "${TUNE_FEATURES:tune-cortexa9-neon} callconvention-hard"
TUNE_FEATURES:tune-cortexa9thf-neon       = "${TUNE_FEATURES:tune-cortexa9t-neon} callconvention-hard"
PACKAGE_EXTRA_ARCHS:tune-cortexa9hf             = "${PACKAGE_EXTRA_ARCHS:tune-armv7ahf} cortexa9hf-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa9thf            = "${PACKAGE_EXTRA_ARCHS:tune-armv7athf} cortexa9hf-vfp cortexa9t2hf-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa9hf-neon        = "${PACKAGE_EXTRA_ARCHS:tune-armv7ahf-neon} cortexa9hf-vfp cortexa9hf-neon"
PACKAGE_EXTRA_ARCHS:tune-cortexa9thf-neon       = "${PACKAGE_EXTRA_ARCHS:tune-armv7athf-neon} cortexa9hf-vfp cortexa9hf-neon cortexa9t2hf-vfp cortexa9t2hf-neon"

# VFPv3 Tunes
AVAILTUNES += "cortexa9-vfpv3 cortexa9t-vfpv3 cortexa9hf-vfpv3 cortexa9thf-vfpv3"
ARMPKGARCH:tune-cortexa9-vfpv3          = "cortexa9"
ARMPKGARCH:tune-cortexa9t-vfpv3         = "cortexa9"
ARMPKGARCH:tune-cortexa9hf-vfpv3        = "cortexa9"
ARMPKGARCH:tune-cortexa9thf-vfpv3       = "cortexa9"
# mcpu is used so don't use armv7a as we don't want march
TUNE_FEATURES:tune-cortexa9-vfpv3           = "${TUNE_FEATURES:tune-cortexa9} vfpv3"
TUNE_FEATURES:tune-cortexa9t-vfpv3          = "${TUNE_FEATURES:tune-cortexa9t} vfpv3"
TUNE_FEATURES:tune-cortexa9hf-vfpv3         = "${TUNE_FEATURES:tune-cortexa9hf} vfpv3"
TUNE_FEATURES:tune-cortexa9thf-vfpv3        = "${TUNE_FEATURES:tune-cortexa9thf} vfpv3"
PACKAGE_EXTRA_ARCHS:tune-cortexa9-vfpv3           = "${PACKAGE_EXTRA_ARCHS:tune-armv7a-vfpv3} cortexa9-vfp cortexa9-vfpv3"
PACKAGE_EXTRA_ARCHS:tune-cortexa9t-vfpv3          = "${PACKAGE_EXTRA_ARCHS:tune-armv7at-vfpv3} cortexa9-vfp cortexa9-vfpv3 cortexa9t2-vfp cortexa9t2-vfpv3"
PACKAGE_EXTRA_ARCHS:tune-cortexa9hf-vfpv3         = "${PACKAGE_EXTRA_ARCHS:tune-armv7ahf-vfpv3} cortexa9hf-vfp cortexa9hf-vfpv3"
PACKAGE_EXTRA_ARCHS:tune-cortexa9thf-vfpv3        = "${PACKAGE_EXTRA_ARCHS:tune-armv7athf-vfpv3} cortexa9hf-vfp cortexa9hf-vfpv3 cortexa9t2hf-vfp cortexa9t2hf-vfpv3"
