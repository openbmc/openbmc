require conf/machine/include/arm/armv8a/tune-cortexa35.inc

MACHINEOVERRIDES =. "corstone1000:"

# TF-A
TFA_PLATFORM = "corstone1000"
EXTRA_IMAGEDEPENDS += "trusted-firmware-a"

TFA_BL2_BINARY = "bl2-corstone1000.bin"
TFA_FIP_BINARY = "fip-corstone1000.bin"

# TF-M
EXTRA_IMAGEDEPENDS += "virtual/trusted-firmware-m"

# TF-M settings for signing host images
TFA_BL2_RE_IMAGE_LOAD_ADDRESS = "0x62353000"
TFA_BL2_RE_SIGN_BIN_SIZE = "0x2d000"
TFA_FIP_RE_IMAGE_LOAD_ADDRESS = "0x68130000"
TFA_FIP_RE_SIGN_BIN_SIZE = "0x00200000"
RE_LAYOUT_WRAPPER_VERSION = "0.0.7"
TFM_SIGN_PRIVATE_KEY = "${libdir}/tfm-scripts/root-RSA-3072_1.pem"
RE_IMAGE_OFFSET = "0x1000"

# u-boot
PREFERRED_VERSION_u-boot ?= "2022.10"
EXTRA_IMAGEDEPENDS += "u-boot"

UBOOT_CONFIG ??= "EFI"
UBOOT_CONFIG[EFI] = "corstone1000_defconfig"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_BOOTARGS = "earlycon=pl011,0x1a510000 console=ttyAMA0 loglevel=9"
UBOOT_ARCH = "arm"
UBOOT_EXTLINUX = "0"

#optee
PREFERRED_VERSION_optee-os ?= "3.18.%"
PREFERRED_VERSION_optee-client ?= "3.18.%"
EXTRA_IMAGEDEPENDS += "optee-os"
OPTEE_ARCH = "arm64"
OPTEE_BINARY = "tee-pager_v2.bin"

# Include smm-gateway and se-proxy SPs into optee-os binary
MACHINE_FEATURES += "ts-smm-gateway ts-se-proxy"
TS_PLATFORM = "arm/corstone1000"

# External System(Cortex-M3)
EXTRA_IMAGEDEPENDS += "external-system"

# Linux kernel
PREFERRED_PROVIDER_virtual/kernel:forcevariable = "linux-yocto"
PREFERRED_VERSION_linux-yocto = "5.19%"
KERNEL_IMAGETYPE = "Image.gz"

INITRAMFS_IMAGE_BUNDLE ?= "1"

#telling the build system which image is responsible of the generation of the initramfs rootfs
INITRAMFS_IMAGE = "corstone1000-initramfs-image"

# add FF-A support in the kernel
MACHINE_FEATURES += "arm-ffa"

# prevent the kernel image from being included in the intramfs rootfs
PACKAGE_EXCLUDE = "kernel-image-*"

# enable this feature for kernel debugging
# MACHINE_FEATURES += "corstone1000_kernel_debug"

# login terminal serial port settings
SERIAL_CONSOLES ?= "115200;ttyAMA0"

# making sure EXTRA_IMAGEDEPENDS will be used while creating the image
WKS_FILE_DEPENDS:append = " ${EXTRA_IMAGEDEPENDS}"

WKS_FILE ?= "corstone1000-image.corstone1000.wks"
