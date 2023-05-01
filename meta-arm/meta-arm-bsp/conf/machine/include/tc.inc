TUNE_FEATURES = "aarch64"

require conf/machine/include/arm/arch-armv8a.inc

MACHINEOVERRIDES =. "tc:"

# Das U-boot
UBOOT_MACHINE ?= "total_compute_defconfig"
PREFERRED_VERSION_u-boot ?= "2022.04"
UBOOT_RD_LOADADDRESS = "0x88000000"
UBOOT_RD_ENTRYPOINT = "0x88000000"
UBOOT_LOADADDRESS = "0x80080000"
UBOOT_ENTRYPOINT = "0x80080000"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-arm64-ack"

# OP-TEE
PREFERRED_VERSION_optee-os ?= "3.18%"
PREFERRED_VERSION_optee-client ?= "3.18%"
PREFERRED_VERSION_optee-test ?= "3.18%"

# Cannot use the default zImage on arm64
KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES += "fitImage"
KERNEL_CLASSES = " kernel-fitimage "

IMAGE_FSTYPES += "cpio.gz"
INITRAMFS_IMAGE ?= "core-image-minimal"

SERIAL_CONSOLES = "115200;ttyAMA0"

EXTRA_IMAGEDEPENDS += "trusted-firmware-a optee-os"
# FIXME - there is signed image dependency/race with testimage.
# This should be fixed in oe-core
TESTIMAGEDEPENDS:append = " virtual/kernel:do_deploy"
