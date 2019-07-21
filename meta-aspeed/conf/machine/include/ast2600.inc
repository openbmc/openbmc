SOC_FAMILY = "aspeed-g6"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc
require conf/machine/include/tune-cortexa7.inc

UBOOT_ENTRYPOINT = "0x80001000"
UBOOT_LOADADDRESS = "0x80001000"
