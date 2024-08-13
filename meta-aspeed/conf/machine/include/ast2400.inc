SOC_FAMILY = "aspeed-g4"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc
require conf/machine/include/arm/armv5/tune-arm926ejs.inc

SERIAL_CONSOLES ?= "115200;ttyS4"
UBOOT_ENTRYPOINT ?= "0x40001000"
UBOOT_LOADADDRESS ?= "0x40001000"

QB_SYSTEM_NAME = "qemu-system-arm"
QB_MEM = "-m 512"
QB_MACHINE = "-machine palmetto-bmc"

PREFERRED_VERSION_u-boot-aspeed-sdk = "v2019.04+%"
PREFERRED_VERSION_u-boot-fw-utils-aspeed-sdk = "v2019.04+%"
