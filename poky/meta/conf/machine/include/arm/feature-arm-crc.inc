# Cyclic Redundancy Check (CRC) instructions for armv8-a and armv8-r

TUNEVALID[crc] = "Enable instructions for ARMv8 Cyclic Redundancy Check (CRC)"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'crc', '+crc', '', d)}"
