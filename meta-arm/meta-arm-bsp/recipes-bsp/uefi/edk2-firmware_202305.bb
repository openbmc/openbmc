SRCREV_edk2           ?= "ba91d0292e593df8528b66f99c1b0b14fadc8e16"
SRCREV_edk2-platforms ?= "be2af02a3fb202756ed9855173e0d0ed878ab6be"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
