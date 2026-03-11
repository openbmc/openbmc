# Juno specific TFA support

COMPATIBLE_MACHINE = "juno"
TFA_PLATFORM = "juno"
TFA_DEBUG = "1"
TFA_MBEDTLS = "1"
TFA_UBOOT ?= "1"
TFA_BUILD_TARGET = "bl1 bl2 bl31 dtbs fip"

# Juno needs the System Control Processor Firmware
DEPENDS += "virtual/control-processor-firmware"

EXTRA_OEMAKE:append = " SCP_BL2=${RECIPE_SYSROOT}/firmware/scp_ramfw.bin"
