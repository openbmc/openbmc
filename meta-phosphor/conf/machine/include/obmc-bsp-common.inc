#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

MACHINE_EXTRA_RRECOMMENDS = " kernel-modules"
EXTRA_IMAGEDEPENDS += "u-boot"

KERNEL_CLASSES ?= "kernel-uimage kernel-cuimage"
KERNEL_IMAGETYPE ?= "cuImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_SUFFIX ?= "bin"

MACHINEOVERRIDES =. "openbmc:"

IMAGE_FSTYPES += "overlay"
IMAGE_FSTYPES += "cpio.${INITRAMFS_CTYPE}.u-boot"
OBMC_IMAGE_EXTRA_INSTALL_append = " u-boot-fw-utils"

IMAGE_CLASSES += "obmc-phosphor-image_types_uboot"

INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES += "cpio.${INITRAMFS_CTYPE}"


