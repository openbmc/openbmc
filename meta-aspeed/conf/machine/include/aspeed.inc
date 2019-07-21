PREFERRED_PROVIDER_virtual/kernel ?= "linux-aspeed"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-aspeed"
PREFERRED_PROVIDER_u-boot ?= "u-boot-aspeed"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-aspeed"

PREFERRED_PROVIDER_virtual/kernel_aspeed-g6 ?= "linux-aspeed-sdk"
PREFERRED_PROVIDER_virtual/bootloader_aspeed-g6 ?= "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot_aspeed-g6 ?= "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot-fw-utils_aspeed-g6 ?= "u-boot-fw-utils-aspeed-sdk"

MACHINEOVERRIDES =. "aspeed:"
