SOC_FAMILY = "aspeed-g7"
include conf/machine/include/soc-family.inc

# U-Boot Settings before aspeed.inc
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-aspeed"
PREFERRED_PROVIDER_u-boot ?= "u-boot-aspeed"
PREFERRED_VERSION_u-boot-aspeed ?= "2026.10"

# fw_printenv/fw_setenv are provided by libubootenv; the fw_env.config
# is installed by the libubootenv bbappend in meta-aspeed.
PREFERRED_PROVIDER_u-boot-fw-utils ?= "libubootenv"

require conf/machine/include/aspeed.inc

DEFAULTTUNE = "cortexa35"

require conf/machine/include/arm/armv8a/tune-cortexa35.inc
require conf/machine/include/bootmcu.inc

UBOOT_FIT_ARM_TRUSTED_FIRMWARE = "1"
UBOOT_FIT_TEE = "1"

SERIAL_CONSOLES ?= "115200;ttyS12"

# For runqemu
QB_SYSTEM_NAME = "qemu-system-aarch64"
QB_MACHINE = "-machine ast2700a1-evb"
QB_MEM = "-m 1G"

# bootloader fitimage
UBOOT_FIT_UBOOT_ENTRYPOINT ?= "0x80000000"
UBOOT_FIT_UBOOT_LOADADDRESS ?= "0x80000000"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS ?= "0xb0000000"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT ?= "0xb0000000"
UBOOT_FIT_TEE_LOADADDRESS ?= "0xb0080000"
UBOOT_FIT_TEE_ENTRYPOINT ?= "0xb0080000"

# kernel fitimage
# Use lzma compressed algorithm instead of gzip to save the code size.
FIT_KERNEL_COMP_ALG ?= "lzma"
FIT_KERNEL_COMP_ALG_EXTENSION ?= ".lzma"

FIT_ADDRESS_CELLS ?= "2"
UBOOT_ENTRYPOINT ?= "0x4 0x00000000"
UBOOT_LOADADDRESS ?= "0x4 0x00000000"

COMPATIBLE_MACHINE:aspeed-g7 = "aspeed-g7"
TFA_PLATFORM = "ast2700"

# optee for BL32.
TFA_SPD = "opteed"

# TFA default build is BL31.
TFA_BUILD_TARGET = "bl31"

# It is a shortcut for "make PLATFORM=aspeed PLATFORM_FLAVOR=ast2700"
OPTEEMACHINE = "aspeed-ast2700"

UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE ?= "${DEPLOY_DIR_IMAGE}/${TFA_BUILD_TARGET}.bin"
UBOOT_FIT_TEE_IMAGE ?= "${DEPLOY_DIR_IMAGE}/optee/tee-raw.bin"

# bootmcu
PREFERRED_PROVIDER_virtual/bootmcu ?= "bootmcu-spl"

