PREFERRED_PROVIDER_virtual/kernel ?= "linux-aspeed"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot ?= "u-boot-aspeed-sdk"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-aspeed-sdk"

# Build a FIT image and stuff core-image-minimal in it by default.
KERNEL_IMAGETYPE ?= "fitImage"
KERNEL_CLASSES ?= "kernel-fitimage"
INITRAMFS_IMAGE ?= "aspeed-image-initramfs"
INITRAMFS_FSTYPES ?= "cpio.xz"

WKS_FILE ?= "emmc-aspeed.wks.in"
WKS_FILE_DEPENDS ?= " \
    phosphor-hostfw-image \
    virtual/bootloader \
    virtual/kernel \
"
WKS_HOSTFW_SIZE ?= "5G"
IMAGE_BOOT_FILES ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE};fitImage"

MACHINE_EXTRA_RRECOMMENDS += "udev-aspeed-vuart udev-aspeed-mtd-partitions"
MACHINE_FEATURES += "hw-rng"

MACHINEOVERRIDES =. "aspeed:"

SERIAL_CONSOLES ?= "115200;ttyS4"

DEFAULTTUNE:aspeed-g5 ?= "arm1176jzs"
DEFAULTTUNE:aspeed-g6 ?= "armv7ahf-vfpv4d16"

UBOOT_ENTRYPOINT:aspeed-g4 ?= "0x40001000"
UBOOT_ENTRYPOINT:aspeed-g5 ?= "0x80001000"
UBOOT_ENTRYPOINT:aspeed-g6 ?= "0x80001000"
UBOOT_LOADADDRESS:aspeed-g4 ?= "0x40001000"
UBOOT_LOADADDRESS:aspeed-g5 ?= "0x80001000"
UBOOT_LOADADDRESS:aspeed-g6 ?= "0x80001000"
