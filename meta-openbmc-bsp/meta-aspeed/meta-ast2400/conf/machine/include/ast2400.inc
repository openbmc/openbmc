#@TYPE: Machine
#@NAME: Aspeed AST2400
#@DESCRIPTION: Common machine configuration for the Aspeed AST2400 Chip

PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"

UBOOT_MACHINE ?= "versatileab_config"
UBOOT_ENTRYPOINT ?= "0x40001000"
UBOOT_LOADADDRESS ?= "0x40001000"

require conf/machine/include/tune-arm926ejs.inc
