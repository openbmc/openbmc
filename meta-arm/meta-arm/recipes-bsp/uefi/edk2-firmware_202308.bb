SRCREV_edk2           ?= "819cfc6b42a68790a23509e4fcc58ceb70e1965e"
SRCREV_edk2-platforms ?= "bb6841e3fd1c60b3f8510b4fc0a380784e05d326"

# FIXME - clang is having issues with antlr
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
