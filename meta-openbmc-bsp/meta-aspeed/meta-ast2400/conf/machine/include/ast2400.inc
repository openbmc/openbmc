#@TYPE: Machine
#@NAME: Aspeed AST2400
#@DESCRIPTION: Common machine configuration for the Aspeed AST2400 Chip

PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"
PREFERRED_VERSION_linux-obmc ?= "4.4+git%"
PREFERRED_VERSION_u-boot ?= "v2013.07+git%"
PREFERRED_VERSION_u-boot-fw-utils ?= "v2013.07+git%"

UBOOT_MACHINE ?= "palmetto_config"
UBOOT_ENTRYPOINT ?= "0x40001000"
UBOOT_LOADADDRESS ?= "0x40001000"

require conf/machine/include/tune-arm926ejs.inc
