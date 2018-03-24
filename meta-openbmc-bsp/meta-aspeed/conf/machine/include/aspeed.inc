PREFERRED_PROVIDER_virtual/kernel ?= "linux-obmc"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-aspeed"
PREFERRED_PROVIDER_u-boot ?= "u-boot-aspeed"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-aspeed"

MACHINEOVERRIDES .= ":aspeed"
