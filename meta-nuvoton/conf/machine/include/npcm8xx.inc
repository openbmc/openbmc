#@TYPE: Machine
#@NAME: Nuvoton NPCM8XX
#@DESCRIPTION: Common machine configuration for Nuvoton NPCM8XX Chip

require conf/machine/include/nuvoton.inc

KERNEL_IMAGETYPE ?= "Image"
KERNEL_EXTRA_ARGS ?= "UIMAGE_LOADADDR=0x00008000"

UBOOT_MACHINE ?= "ArbelEVB_defconfig"
UBOOT_ENTRYPOINT ?= "0"
UBOOT_LOADADDRESS ?= "0"

FLASH_SIZE = "32768"
FLASH_UBOOT_OFFSET = "0"
FLASH_KERNEL_OFFSET = "2048"
FLASH_ROFS_OFFSET = "8192"
FLASH_RWFS_OFFSET = "31744"

# UBI volume sizes in KB unless otherwise noted.
FLASH_UBI_RWFS_SIZE = "6144"
FLASH_UBI_RWFS_TXT_SIZE = "6MiB"

SERIAL_CONSOLES = "115200;ttyS0"

SECURED_OS = "True"

SOC_FAMILY = "npcm8xx"
include conf/machine/include/soc-family.inc
MACHINEOVERRIDES .= ":npcm8xx"

require conf/machine/include/arm/armv8a/tune-cortexa35.inc
