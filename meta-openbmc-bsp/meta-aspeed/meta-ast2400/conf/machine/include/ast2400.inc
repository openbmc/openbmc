#@TYPE: Machine
#@NAME: Aspeed AST2400
#@DESCRIPTION: Common machine configuration for the Aspeed AST2400 Chip

PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"
PREFERRED_VERSION_u-boot ?= "v2016.07+git%"
PREFERRED_VERSION_u-boot-fw-utils ?= "v2016.07+git%"

UBOOT_MACHINE ?= "ast_g4_ncsi_config"
UBOOT_ENTRYPOINT ?= "0x40001000"
UBOOT_LOADADDRESS ?= "0x40001000"

MACHINE_EXTRA_RDEPENDS += "udev-aspeed-vuart"

SERIAL_CONSOLES = "115200;ttyS4"

MACHINEOVERRIDES .= ":ast2400"

require conf/machine/include/tune-arm926ejs.inc
