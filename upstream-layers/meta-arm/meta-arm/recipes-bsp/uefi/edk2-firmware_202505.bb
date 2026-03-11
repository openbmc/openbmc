require recipes-bsp/uefi/edk2-firmware.inc

SRCREV_edk2           ?= "6951dfe7d59d144a3a980bd7eda699db2d8554ac"
SRCREV_edk2-platforms ?= "564f6509e89f45b25d97db2772ca9b3bec8b3ed5"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

SRC_URI += " file://edk2_fix_epoch.patch"
