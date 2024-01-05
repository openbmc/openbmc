SRCREV_edk2           ?= "8736b8fdca85e02933cdb0a13309de14c9799ece"
SRCREV_edk2-platforms ?= "d61836283a4c9198a02387fe7b31a8242e732f3f"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
