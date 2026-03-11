# Advanced SIMD and floating-point instructions for armv7-a, armv7ve,
# armv8-a, armv8.1-a, armv8.3-a, armv8.4-a, armv8.5-a, armv8.6-a, and armv8-r

TUNEVALID[simd] = "Enable instructions for Advanced SIMD and floating-point units"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'simd', '+simd', '', d)}"
