#@TYPE: Machine
#@NAME: Aspeed AST2500
#@DESCRIPTION: Common machine configuration for the Aspeed AST2500 Chip

require conf/machine/include/aspeed.inc

UBOOT_MACHINE ?= "ast_g5_ncsi_config"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

MACHINE_EXTRA_RDEPENDS += "udev-aspeed-vuart"

DEFAULTTUNE ?= "arm1176jzs"

SERIAL_CONSOLES = "115200;ttyS4"

SOC_FAMILY = "aspeed-g5"
include conf/machine/include/soc-family.inc
MACHINEOVERRIDES .= ":ast2500"

require conf/machine/include/tune-arm1176jz-s.inc
