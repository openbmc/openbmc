PREFERRED_PROVIDER_virtual/kernel ?= "linux-nuvoton"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-nuvoton"

WKS_FILE ?= "emmc-nuvoton.wks.in"
WKS_FILE_DEPENDS ?= " \
    virtual/kernel \
"
WKS_RWFS_SIZE ??= "256M"
WKS_ROFS_SIZE ??= "256M"
IMAGE_BOOT_FILES ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE};fitImage"
IMAGE_FSTYPES:remove:df-phosphor-mmc = "mtd-static mtd-static-tar mtd-static-alltar"

MACHINEOVERRIDES .= ":nuvoton"
MACHINE_EXTRA_RRECOMMENDS += "udev-nuvoton-mtd-partitions"
MACHINE_FEATURES += "hw-rng"
