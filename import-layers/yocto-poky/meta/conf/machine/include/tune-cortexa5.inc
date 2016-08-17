DEFAULTTUNE ?= "armv7a-neon"

require conf/machine/include/arm/arch-armv7a.inc

TUNEVALID[cortexa5] = "Enable Cortex-A5 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa5', ' -mcpu=cortex-a5', '', d)}"

# Little Endian base configs
AVAILTUNES += "cortexa5 cortexa5t cortexa5-neon cortexa5t-neon"
ARMPKGARCH_tune-cortexa5             = "cortexa5"
ARMPKGARCH_tune-cortexa5t            = "cortexa5"
ARMPKGARCH_tune-cortexa5-neon        = "cortexa5"
ARMPKGARCH_tune-cortexa5t-neon       = "cortexa5"
TUNE_FEATURES_tune-cortexa5             = "${TUNE_FEATURES_tune-armv7a}             cortexa5"
TUNE_FEATURES_tune-cortexa5t            = "${TUNE_FEATURES_tune-armv7at}            cortexa5"
TUNE_FEATURES_tune-cortexa5-neon        = "${TUNE_FEATURES_tune-armv7a-neon}        cortexa5"
TUNE_FEATURES_tune-cortexa5t-neon       = "${TUNE_FEATURES_tune-armv7at-neon}       cortexa5"
PACKAGE_EXTRA_ARCHS_tune-cortexa5             = "${PACKAGE_EXTRA_ARCHS_tune-armv7a}  cortexa5-vfp"
PACKAGE_EXTRA_ARCHS_tune-cortexa5t            = "${PACKAGE_EXTRA_ARCHS_tune-armv7at} cortexa5-vfp cortexa5t2-vfp"
PACKAGE_EXTRA_ARCHS_tune-cortexa5-neon        = "${PACKAGE_EXTRA_ARCHS_tune-armv7a-neon}  cortexa5-vfp cortexa5-neon"
PACKAGE_EXTRA_ARCHS_tune-cortexa5t-neon       = "${PACKAGE_EXTRA_ARCHS_tune-armv7at-neon} cortexa5-vfp cortexa5-neon cortexa5t2-vfp cortexa5t2-neon"

# HF Tunes
AVAILTUNES += "cortexa5hf cortexa5thf cortexa5hf-neon cortexa5thf-neon"
ARMPKGARCH_tune-cortexa5hf             = "cortexa5"
ARMPKGARCH_tune-cortexa5thf            = "cortexa5"
ARMPKGARCH_tune-cortexa5hf-neon        = "cortexa5"
ARMPKGARCH_tune-cortexa5thf-neon       = "cortexa5"
TUNE_FEATURES_tune-cortexa5hf             = "${TUNE_FEATURES_tune-armv7ahf}             cortexa5"
TUNE_FEATURES_tune-cortexa5thf            = "${TUNE_FEATURES_tune-armv7athf}            cortexa5"
TUNE_FEATURES_tune-cortexa5hf-neon        = "${TUNE_FEATURES_tune-armv7ahf-neon}        cortexa5"
TUNE_FEATURES_tune-cortexa5thf-neon       = "${TUNE_FEATURES_tune-armv7athf-neon}       cortexa5"
PACKAGE_EXTRA_ARCHS_tune-cortexa5hf             = "${PACKAGE_EXTRA_ARCHS_tune-armv7ahf}  cortexa5hf-vfp"
PACKAGE_EXTRA_ARCHS_tune-cortexa5thf            = "${PACKAGE_EXTRA_ARCHS_tune-armv7athf} cortexa5hf-vfp cortexa5t2hf-vfp"
PACKAGE_EXTRA_ARCHS_tune-cortexa5hf-neon        = "${PACKAGE_EXTRA_ARCHS_tune-armv7ahf-neon}  cortexa5hf-vfp cortexa5hf-neon"
PACKAGE_EXTRA_ARCHS_tune-cortexa5thf-neon       = "${PACKAGE_EXTRA_ARCHS_tune-armv7athf-neon} cortexa5hf-vfp cortexa5hf-neon cortexa5t2hf-vfp cortexa5t2hf-neon"
