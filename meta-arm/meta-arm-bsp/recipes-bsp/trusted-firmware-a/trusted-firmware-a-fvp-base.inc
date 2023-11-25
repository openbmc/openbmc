# FVP specific TFA parameters

#
# Armv8-A Base Platform FVP
#

FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"
SRC_URI:append = " file://0001-fdts-fvp-base-Add-stdout-path-and-virtio-net-and-rng.patch"

COMPATIBLE_MACHINE = "fvp-base"
TFA_PLATFORM = "fvp"
TFA_DEBUG = "1"
TFA_MBEDTLS = "1"
TFA_UBOOT ?= "1"
TFA_BUILD_TARGET = "bl1 bl2 bl31 dtbs fip"

EXTRA_OEMAKE += "FVP_DT_PREFIX=fvp-base-gicv3-psci-1t"

# Our fvp-base machine explicitly has v8.4 cores
EXTRA_OEMAKE += "ARM_ARCH_MAJOR=8 ARM_ARCH_MINOR=4"
