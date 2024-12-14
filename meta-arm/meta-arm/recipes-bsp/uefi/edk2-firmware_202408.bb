SRCREV_edk2           ?= "b158dad150bf02879668f72ce306445250838201"
SRCREV_edk2-platforms ?= "a3c898956a4d48dc5980336fa6ce6eeb23c4f72b"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
