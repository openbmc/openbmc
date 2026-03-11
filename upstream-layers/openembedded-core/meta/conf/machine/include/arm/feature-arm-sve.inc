# Scalable Vector Extension (SVE) for Armv8-A and R
# Enabled by default for Armv9

TUNEVALID[sve] = "Enable SVE instructions for ARMv8"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'sve', '+sve', '', d)}"

TUNEVALID[sve2] = "Enable SVE2 instructions for ARMv8"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'sve2', '+sve2', '', d)}"
