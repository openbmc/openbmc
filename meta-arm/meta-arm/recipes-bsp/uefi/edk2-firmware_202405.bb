SRCREV_edk2           ?= "3e722403cd16388a0e4044e705a2b34c841d76ca"
SRCREV_edk2-platforms ?= "59c686673992d7549c2b054773d5d1b5e739a88b"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
