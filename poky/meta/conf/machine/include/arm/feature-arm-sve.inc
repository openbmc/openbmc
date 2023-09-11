# Scalable Vector Extension (SVE) for:
# armv8.2-a, armv8.3-a, armv8.4-a, armv8.5-a, armv8.6-a, armv8-r, armv9a

TUNEVALID[sve] = "Enable SVE instructions for ARMv8 and ARMv9"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'sve', '+sve', '', d)}"
