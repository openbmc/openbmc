OVERRIDES .= ":vegman"
KMACHINE = "aspeed"
KERNEL_DEVICETREE = "aspeed/${KMACHINE}-bmc-${MACHINE}.dtb"
UBOOT_MACHINE = "evb-ast2500_defconfig"
UBOOT_DEVICETREE = "ast2500-evb"

require conf/machine/include/ast2500.inc
include conf/machine/include/vegman-bsp.inc
require ${@bb.utils.contains('BSP_TYPE', 'vegman', '', 'conf/machine/include/obmc-bsp-common.inc', d)}

SERIAL_CONSOLES = "115200;ttyS3 115200;ttyS4"
FLASH_SIZE = "65536"

IMAGE_FEATURES += "allow-root-login"

MACHINE_FEATURES += " \
    obmc-chassis-state-mgmt \
    obmc-host-ipmi \
    obmc-host-state-mgmt \
    obmc-phosphor-chassis-mgmt \
    obmc-phosphor-fan-mgmt \
    obmc-phosphor-flash-mgmt \
"

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-discover-system-state ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "dbus-sensors"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-yadro-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-yadro-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-yadro-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-yadro-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-kcs"

PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native = "vegman-led-manager-config-native"

PREFERRED_PROVIDER_virtual/bootloader = "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot = "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-aspeed-sdk"
