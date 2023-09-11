# Corstone-500 specific TFA support

COMPATIBLE_MACHINE = "corstone500"
TFA_PLATFORM = "a5ds"
TFA_DEBUG = "1"
TFA_UBOOT = "1"
TFA_BUILD_TARGET = "all fip"
TFA_INSTALL_TARGET = "bl1.bin fip.bin"

EXTRA_OEMAKE:append = " \
                    ARCH=aarch32 \
                    FVP_HW_CONFIG_DTS=fdts/a5ds.dts \
                    ARM_ARCH_MAJOR=7 \
                    AARCH32_SP=sp_min \
                    ARM_CORTEX_A5=yes \
                    ARM_XLAT_TABLES_LIB_V1=1 \
                    "
