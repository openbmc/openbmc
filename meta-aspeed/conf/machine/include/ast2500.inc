#@TYPE: Machine
#@NAME: Aspeed AST2500
#@DESCRIPTION: Common machine configuration for the Aspeed AST2500 Chip

SOC_FAMILY = "aspeed-g5"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc

DEFAULTTUNE ?= "arm1176jzs"
require conf/machine/include/tune-arm1176jz-s.inc

UBOOT_MACHINE ?= "ast_g5_ncsi_config"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

MACHINE_EXTRA_RDEPENDS += "udev-aspeed-vuart"

SERIAL_CONSOLES = "115200;ttyS4"
