SRCREV_edk2           ?= "edc6681206c1a8791981a2f911d2fb8b3d2f5768"
SRCREV_edk2-platforms ?= "07842635c80b64c4a979a652104ea1141ba5007a"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
