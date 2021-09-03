# GCC supports following options:
# 'vfp', 'vfpv3', 'vfpv3-fp16', 'vfpv3-d16', 'vfpv3-d16-fp16', 'vfpv3xd', 'vfpv3xd-fp16', 'neon', 'neon-fp16', 'vfpv4', 'vfpv4-d16', 'fpv4-sp-d16', 'neon-vfpv4', 'fpv5-d16', 'fpv5-sp-d16', 'fp-armv8', 'neon-fp-armv8', and 'crypto-neon-fp-armv8'

# Only some combinations are supported here.
# 'vfp', -mfloat-abi parameter and 'hf' suffix is implemented in feature-arm-vfp.inc

TUNEVALID[neon] = "Enable Neon SIMD accelerator unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'neon', ' neon', '', d)}"

TUNEVALID[vfpv3d16] = "Enable Vector Floating Point Version 3 with 16 registers (vfpv3-d16) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv3d16', ' vfpv3-d16', '', d)}"

TUNEVALID[vfpv3] = "Enable Vector Floating Point Version 3 with 32 registers (vfpv3) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv3', ' vfpv3', '' , d)}"

TUNEVALID[vfpv4] = "Enable Vector Floating Point Version 4 (vfpv4) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv4', ' vfpv4', '', d)}"
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', [ 'vfpv4', 'neon' ], ' neon-vfpv4', '', d)}"

TUNEVALID[vfpv4d16] = "Enable Vector Floating Point Version 4 with 16 registers (vfpv4-d16) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv4d16', ' vfpv4-d16', '', d)}"

TUNEVALID[vfpv5spd16] = "Enable Vector Floating Point Version 5, Single Precision. with 16 registers (fpv5-sp-d16) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfpv5spd16', 'fpv5-sp-d16', '', d)}"

TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', [ 'vfpv3d16', 'vfpv5spd16' ], '+fp', '', d)}"
