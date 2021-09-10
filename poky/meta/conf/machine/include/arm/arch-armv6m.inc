# Tuning for ARMV6-m defined in ARM v6-M ArchitectureReference Manual
# at https://static.docs.arm.com/ddi0419/d/DDI0419D_armv6m_arm.pdf
DEFAULTTUNE ?= "armv6m"

TUNEVALID[armv6m] = "Enable instructions for ARMv6-m"
TUNECONFLICTS[armv6m] = "armv4 armv5 armv6 armv7a"

# Use armv6s-m instead of armv6-m to avoid gcc bug "SVC is not permitted on this architecture".
# SVC is a valid instruction.
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv6m', ' -march=armv6s-m', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv6m', 'armv6m:', '', d)}"

require conf/machine/include/arm/arch-armv5.inc

# Little Endian
AVAILTUNES += "armv6m"
ARMPKGARCH:tune-armv6m             = "armv6m"
TUNE_FEATURES:tune-armv6m          = "armv6m"
PACKAGE_EXTRA_ARCHS:tune-armv6m    = "armv6m"
