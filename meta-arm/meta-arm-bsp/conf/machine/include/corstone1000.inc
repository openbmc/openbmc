require conf/machine/include/arm/armv8a/tune-cortexa35.inc

MACHINEOVERRIDES =. "corstone1000:"

# TF-M
PREFERRED_VERSION_trusted-firmware-m ?= "2.1.%"

# TF-A
TFA_PLATFORM = "corstone1000"
PREFERRED_VERSION_trusted-firmware-a ?= "2.11.%"
PREFERRED_VERSION_tf-a-tests ?= "2.10.%"

TFA_BL2_BINARY = "bl2-corstone1000.bin"
TFA_FIP_BINARY = "fip-corstone1000.bin"

# optee
PREFERRED_VERSION_optee-os ?= "4.3.%"

# Trusted Services
TS_PLATFORM = "arm/corstone1000"
TS_SP_SE_PROXY_CONFIG = "corstone1000"
# Include smm-gateway and se-proxy SPs into optee-os binary
MACHINE_FEATURES += "ts-smm-gateway ts-se-proxy"

# u-boot
PREFERRED_VERSION_u-boot ?= "2023.07%"
MACHINE_FEATURES += "efi"
EFI_PROVIDER ?= "grub-efi"

# Grub
LINUX_KERNEL_ARGS ?= "earlycon=pl011,0x1a510000 console=ttyAMA0,115200"
GRUB_LINUX_APPEND ?= "${LINUX_KERNEL_ARGS}"
IMAGE_CMD:wic[vardeps] += "GRUB_LINUX_APPEND"

# Linux kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
PREFERRED_VERSION_linux-yocto ?= "6.12.%"
KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPE:firmware = "Image.gz"
# add FF-A support in the kernel
MACHINE_FEATURES += "arm-ffa"
# enable this feature for kernel debugging
# MACHINE_FEATURES += "corstone1000_kernel_debug"

# login terminal serial port settings
SERIAL_CONSOLES ?= "115200;ttyAMA0"

IMAGE_FSTYPES += "wic"
# Need to clear the suffix so TESTIMAGE_AUTO works
IMAGE_NAME_SUFFIX = ""
WKS_FILE ?= "efi-disk-no-swap.wks.in"
WKS_FILE:firmware ?= "corstone1000-flash-firmware.wks.in"

# making sure EXTRA_IMAGEDEPENDS will be used while creating the image
WKS_FILE_DEPENDS:append = " ${EXTRA_IMAGEDEPENDS}"

# If not building under the firmware multiconf we need to build the actual firmware
FIRMWARE_DEPLOYMENT ?= "firmware-deploy-image"
FIRMWARE_DEPLOYMENT:firmware ?= ""
EXTRA_IMAGEDEPENDS += "${FIRMWARE_DEPLOYMENT}"

ARM_SYSTEMREADY_FIRMWARE = "${FIRMWARE_DEPLOYMENT}:do_deploy \
                            corstone1000-esp-image:do_image_complete \
                           "
ARM_SYSTEMREADY_ACS_CONSOLE ?= "default"

# Workaround IMAGE_ROOTFS_EXTRA_SPACE being ignored when images are repacked
IMAGE_ROOTFS_EXTRA_ARGS += "--extra-space ${@${IMAGE_ROOTFS_EXTRA_SPACE}}K"
