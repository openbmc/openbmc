# Armv7-A FVP specific TFA parameters

COMPATIBLE_MACHINE = "fvp-base-arm32"
TFA_PLATFORM = "fvp"
TFA_UBOOT = "1"
TFA_BUILD_TARGET = "dtbs bl1 bl32 fip"

EXTRA_OEMAKE:append = " \
    ARCH=aarch32 \
    AARCH32_SP=sp_min \
    "

