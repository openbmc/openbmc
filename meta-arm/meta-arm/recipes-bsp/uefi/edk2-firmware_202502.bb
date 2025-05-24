require recipes-bsp/uefi/edk2-firmware.inc

SRCREV_edk2           ?= "fbe0805b2091393406952e84724188f8c1941837"
SRCREV_edk2-platforms ?= "728c8bb974be69b4034fad7a1c60917cca2dd03d"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

SRC_URI += " file://edk2_fix_epoch.patch"
