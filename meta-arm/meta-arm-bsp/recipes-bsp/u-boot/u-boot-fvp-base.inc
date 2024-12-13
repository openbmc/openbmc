# FVP base specific U-boot support

SRC_URI:append = " \
    file://0001-vexpress64-Set-the-DM_RNG-property.patch \
    file://0002-vexpress64-Select-PSCI-RESET-by-default.patch \
    file://0003-vexpress64-Imply-CONFIG_ARM64_CRC32-by-default.patch \
    file://0004-arm-Move-sev-and-wfe-definitions-to-common-Arm-heade.patch \
    file://0005-armv8-generic_timer-Use-event-stream-for-udelay.patch \
    "
