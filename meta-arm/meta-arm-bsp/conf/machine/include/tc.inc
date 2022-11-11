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
# Below options will generate a key to sign the kernel Image and INITRAMFS_IMAGE
# according to the default parameters of kernel-fitimage.bbclass. If the user
# would prefer to use their own keys, disable the key generation using the
# FIT_GENERATE_KEYS parameter and specify the location of the keys using the
# below paramters.
UBOOT_SIGN_ENABLE = "1"
UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb"
UBOOT_SIGN_KEYNAME = "dev_key"
UBOOT_SIGN_KEYDIR = "${DEPLOY_DIR_IMAGE}/keys"
FIT_GENERATE_KEYS = "1"

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
