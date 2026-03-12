SRCREV_edk2           ?= "b158dad150bf02879668f72ce306445250838201"
SRCREV_edk2-platforms ?= "a3c898956a4d48dc5980336fa6ce6eeb23c4f72b"

SRC_URI += "file://0001-Platform-StMmRpmb-Fix-build.patch;patchdir=edk2-platforms \
            file://0001-BaseTools-Pccts-set-C-standard.patch"

# FIXME:
# ArmPkg/Universal/Smbios/SmbiosMiscDxe/Type03/MiscChassisManufacturerFunction.c:146:37:
# error: variable 'ContainedElements' is uninitialized when passed as a const pointer argument here [-Werror,-Wuninitialized-const-pointer]
TOOLCHAIN:aarch64 = "gcc"

require recipes-bsp/uefi/edk2-firmware.inc
