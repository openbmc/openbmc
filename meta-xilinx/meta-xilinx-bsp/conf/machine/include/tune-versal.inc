DEFAULTTUNE ?= "aarch64"
SOC_FAMILY ?= "versal"

# Available SOC_VARIANT's for versal:
# virt

SOC_VARIANT ?= ""

require conf/machine/include/arm/arch-armv8.inc
require conf/machine/include/soc-family.inc

# Linux Configuration
KERNEL_IMAGETYPE ?= "Image"

