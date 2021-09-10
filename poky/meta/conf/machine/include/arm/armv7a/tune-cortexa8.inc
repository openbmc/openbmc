DEFAULTTUNE ?= "cortexa8thf-neon"

require conf/machine/include/arm/arch-armv7a.inc

TUNEVALID[cortexa8] = "Enable Cortex-A8 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa8', ' -mcpu=cortex-a8', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'cortexa8', 'armv7a:', '', d)}"

# Little Endian base configs
AVAILTUNES += "cortexa8 cortexa8t cortexa8-neon cortexa8t-neon"
ARMPKGARCH:tune-cortexa8             = "cortexa8"
ARMPKGARCH:tune-cortexa8t            = "cortexa8"
ARMPKGARCH:tune-cortexa8-neon        = "cortexa8"
ARMPKGARCH:tune-cortexa8t-neon       = "cortexa8"
# mcpu is used so don't use armv7a as we don't want march
TUNE_FEATURES:tune-cortexa8             = "arm vfp cortexa8"
TUNE_FEATURES:tune-cortexa8t            = "${TUNE_FEATURES:tune-cortexa8} thumb"
TUNE_FEATURES:tune-cortexa8-neon        = "${TUNE_FEATURES:tune-cortexa8} neon"
TUNE_FEATURES:tune-cortexa8t-neon       = "${TUNE_FEATURES:tune-cortexa8-neon} thumb"
PACKAGE_EXTRA_ARCHS:tune-cortexa8             = "${PACKAGE_EXTRA_ARCHS:tune-armv7a} cortexa8-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa8t            = "${PACKAGE_EXTRA_ARCHS:tune-armv7at} cortexa8-vfp cortexa8t2-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa8-neon        = "${PACKAGE_EXTRA_ARCHS:tune-armv7a-neon} cortexa8-vfp cortexa8-neon"
PACKAGE_EXTRA_ARCHS:tune-cortexa8t-neon       = "${PACKAGE_EXTRA_ARCHS:tune-armv7at-neon} cortexa8-vfp cortexa8-neon cortexa8t2-vfp cortexa8t2-neon"

# HF Tunes
AVAILTUNES += "cortexa8hf cortexa8thf cortexa8hf-neon cortexa8thf-neon"
ARMPKGARCH:tune-cortexa8hf             = "cortexa8"
ARMPKGARCH:tune-cortexa8thf            = "cortexa8"
ARMPKGARCH:tune-cortexa8hf-neon        = "cortexa8"
ARMPKGARCH:tune-cortexa8thf-neon       = "cortexa8"
# mcpu is used so don't use armv7a as we don't want march
TUNE_FEATURES:tune-cortexa8hf             = "${TUNE_FEATURES:tune-cortexa8} callconvention-hard"
TUNE_FEATURES:tune-cortexa8thf            = "${TUNE_FEATURES:tune-cortexa8t} callconvention-hard"
TUNE_FEATURES:tune-cortexa8hf-neon        = "${TUNE_FEATURES:tune-cortexa8-neon} callconvention-hard"
TUNE_FEATURES:tune-cortexa8thf-neon       = "${TUNE_FEATURES:tune-cortexa8t-neon} callconvention-hard"
PACKAGE_EXTRA_ARCHS:tune-cortexa8hf             = "${PACKAGE_EXTRA_ARCHS:tune-armv7ahf} cortexa8hf-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa8thf            = "${PACKAGE_EXTRA_ARCHS:tune-armv7athf} cortexa8hf-vfp cortexa8t2hf-vfp"
PACKAGE_EXTRA_ARCHS:tune-cortexa8hf-neon        = "${PACKAGE_EXTRA_ARCHS:tune-armv7ahf-neon} cortexa8hf-vfp cortexa8hf-neon"
PACKAGE_EXTRA_ARCHS:tune-cortexa8thf-neon       = "${PACKAGE_EXTRA_ARCHS:tune-armv7athf-neon} cortexa8hf-vfp cortexa8hf-neon cortexa8t2hf-vfp cortexa8t2hf-neon"
