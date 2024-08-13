SOC_FAMILY = "aspeed-g6"

# Enable SPL signing by default
SOCSEC_SIGN_ENABLE ?= "1"
SOCSEC_SIGN_SOC ?= "2600"

include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc

DEFAULTTUNE = "armv7ahf-vfpv4d16"
require conf/machine/include/arm/arch-armv7a.inc

SERIAL_CONSOLES ?= "115200;ttyS4"
UBOOT_ENTRYPOINT ?= "0x80001000"
UBOOT_LOADADDRESS ?= "0x80001000"

QB_SYSTEM_NAME = "qemu-system-arm"
QB_MACHINE = "-machine ast2600-evb"
QB_MEM = "-m 1G"

PREFERRED_VERSION_u-boot-aspeed-sdk = "v2019.04+%"
PREFERRED_VERSION_u-boot-fw-utils-aspeed-sdk = "v2019.04+%"
