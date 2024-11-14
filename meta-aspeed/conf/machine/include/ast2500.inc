SOC_FAMILY = "aspeed-g5"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc

DEFAULTTUNE = "arm1176jzs"
require conf/machine/include/arm/armv6/tune-arm1176jz-s.inc

SERIAL_CONSOLES ?= "115200;ttyS4"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

QB_MACHINE = "-machine ast2500-evb"
QB_MEM = "-m 1G"