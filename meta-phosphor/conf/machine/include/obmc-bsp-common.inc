#@TYPE: Machine
#@NAME: OpenBMC
#@DESCRIPTION: Common machine configuration for OpenBMC chips

MACHINE_EXTRA_RRECOMMENDS = " kernel-modules"
EXTRA_IMAGEDEPENDS += "u-boot"

KERNEL_CLASSES ?= "kernel-uimage kernel-cuimage"
KERNEL_IMAGETYPE ?= "cuImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

UBOOT_SUFFIX ?= "bin"
IMAGE_BOOT_FILES ?= "u-boot.${UBOOT_SUFFIX}"

MACHINEOVERRIDES =. "openbmc:"
