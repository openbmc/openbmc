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

# runqemu support
EXTRA_IMAGEDEPENDS += "qemu-helper-native"
IMAGE_CLASSES += "qemuboot"
QB_SYSTEM_NAME = "qemu-system-arm"
QB_ROOTFS_OPT = "-drive file=@ROOTFS@,if=mtd,format=raw"
QB_DEFAULT_FSTYPE = "static.mtd"
# Disable virtio-pci device
QB_RNG = ""
# Specify netdev to align with default QB_SLIRP_OPT
QB_NETWORK_DEVICE = "-net nic,netdev=net0"
# Boot from drive
QB_DEFAULT_KERNEL = "none"
