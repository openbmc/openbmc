#@TYPE: Machine
#@NAME: Aspeed AST2500
#@DESCRIPTION: Common machine configuration for the Aspeed AST2500 Chip

PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"

UBOOT_MACHINE ?= "ast_g5_ncsi_config"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

MACHINE_EXTRA_RDEPENDS += "udev-aspeed-vuart"

DEFAULTTUNE = "armv6-novfp"

SERIAL_CONSOLES = "115200;ttyS4"

require conf/machine/include/arm/arch-armv6.inc
