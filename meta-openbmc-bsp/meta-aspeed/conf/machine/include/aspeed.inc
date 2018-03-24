PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"

ASPEED_UBOOT_VERSION ?= "v2016.07+git%"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"
PREFERRED_VERSION_u-boot ?= "${ASPEED_UBOOT_VERSION}"
PREFERRED_VERSION_u-boot-fw-utils ?= "${ASPEED_UBOOT_VERSION}"

MACHINEOVERRIDES .= ":aspeed"
