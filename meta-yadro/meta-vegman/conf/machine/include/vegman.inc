OVERRIDES .= ":vegman"
KMACHINE = "aspeed"
KERNEL_DEVICETREE = "${KMACHINE}-bmc-${MACHINE}.dtb"
UBOOT_MACHINE = "evb-ast2500_defconfig"
UBOOT_DEVICETREE = "ast2500-evb"

require conf/machine/include/ast2500.inc
include conf/machine/include/vegman-bsp.inc
require ${@bb.utils.contains('BSP_TYPE', 'vegman', '', 'conf/machine/include/obmc-bsp-common.inc', d)}

SERIAL_CONSOLES = "115200;ttyS3 115200;ttyS4"
FLASH_SIZE = "65536"

IMAGE_FEATURES += "allow-root-login"

PREFERRED_PROVIDER_virtual/bootloader = "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot = "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-aspeed-sdk"
