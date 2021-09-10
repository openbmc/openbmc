# Define the 'qemu-sd' conversion type
#
# This conversion type pads any image to the 512K boundary to ensure that the
# image file can be used directly with QEMU's SD emulation which requires the
# block device to match that of valid SD card sizes (which are multiples of
# 512K).

CONVERSIONTYPES_append = " qemu-sd"
CONVERSION_CMD_qemu-sd = "cp ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.qemu-sd; truncate -s %512K ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.qemu-sd"
CONVERSION_DEPENDS_qemu-sd = "coreutils-native"
