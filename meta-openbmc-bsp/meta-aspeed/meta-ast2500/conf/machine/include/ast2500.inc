#@TYPE: Machine
#@NAME: Aspeed AST2500
#@DESCRIPTION: Common machine configuration for the Aspeed AST2500 Chip

PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"

UBOOT_MACHINE ?= "ast_g5_config"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

INITRD_IMAGE_ENTRYPOINT = "0x81000000"
INITRD_IMAGE_LOADADDRESS = "0x81000000"

DEFAULTTUNE = "armv6-novfp"
require conf/machine/include/arm/arch-armv6.inc
