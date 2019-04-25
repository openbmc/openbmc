#@TYPE: Machine
#@NAME: Aspeed AST2400
#@DESCRIPTION: Common machine configuration for the Aspeed AST2400 Chip

SOC_FAMILY = "aspeed-g4"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc
require conf/machine/include/tune-arm926ejs.inc

UBOOT_MACHINE ?= "ast_g4_ncsi_config"
UBOOT_ENTRYPOINT ?= "0x40001000"
UBOOT_LOADADDRESS ?= "0x40001000"

MACHINE_EXTRA_RDEPENDS += "udev-aspeed-vuart"

SERIAL_CONSOLES = "115200;ttyS4"
