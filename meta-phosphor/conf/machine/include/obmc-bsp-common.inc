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

IMAGE_CLASSES += "obmc-phosphor-image_types_uboot"
IMAGE_CTYPE = "lzma"
IMAGE_FSTYPES += "cpio.${IMAGE_CTYPE}.u-boot"
