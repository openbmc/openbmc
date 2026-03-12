require recipes-bsp/uefi/edk2-firmware.inc

SRCREV_edk2           ?= "b7a715f7c03c45c6b4575bf88596bfd79658b8ce"
SRCREV_edk2-platforms ?= "75024f5aa54cf4d975d024768604b28c754db338"

SRC_URI += "file://0001-BaseTools-Source-C-VfrCompile-Fix-parallel-make-fail.patch"
