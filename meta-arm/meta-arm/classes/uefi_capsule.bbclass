# This class generates UEFI capsules
# The current class supports generating a capsule with single firmware binary

IMAGE_TYPES += "uefi_capsule"

# u-boot-tools should be installed in the native sysroot directory
do_image_uefi_capsule[depends] += "u-boot-tools-native:do_populate_sysroot"

# By default the wic image is used to create a capsule
CAPSULE_IMGTYPE ?= "wic"

# IMGDEPLOYDIR is used as the default location of firmware binary for which the capsule needs to be created
CAPSULE_IMGLOCATION ?= "${IMGDEPLOYDIR}"

# The generated capsule by default has uefi.capsule extension
CAPSULE_EXTENSION ?= "uefi.capsule"

# The generated capsule's name by default is the same as UEFI_FIRMWARE_BINARY
CAPSULE_NAME ?= "${UEFI_FIRMWARE_BINARY}"

# The following variables must be set to be able to generate a capsule update
CAPSULE_CERTIFICATE_PATH ?= ""
CAPSULE_FW_VERSION ?= ""
CAPSULE_GUID ?= ""
CAPSULE_INDEX ?= ""
CAPSULE_MONOTONIC_COUNT ?= ""
CAPSULE_PRIVATE_KEY_PATH ?= ""
UEFI_FIRMWARE_BINARY ?= ""

# Check if the required variables are set
python() {
    for var in ["CAPSULE_CERTIFICATE_PATH", "CAPSULE_FW_VERSION", \
                "CAPSULE_GUID", "CAPSULE_INDEX", \
                "CAPSULE_MONOTONIC_COUNT", "CAPSULE_PRIVATE_KEY_PATH", \
                "UEFI_FIRMWARE_BINARY"]:
        if not d.getVar(var):
            raise bb.parse.SkipRecipe(f"{var} not set")
}

IMAGE_CMD:uefi_capsule(){
    mkeficapsule --certificate ${CAPSULE_CERTIFICATE_PATH} \
                 --fw-version ${CAPSULE_FW_VERSION} \
                 --guid ${CAPSULE_GUID} \
                 --index ${CAPSULE_INDEX} \
                 --monotonic-count ${CAPSULE_MONOTONIC_COUNT} \
                 --private-key ${CAPSULE_PRIVATE_KEY_PATH} \
                 ${UEFI_FIRMWARE_BINARY} \
                 ${CAPSULE_IMGLOCATION}/${CAPSULE_NAME}.${CAPSULE_EXTENSION}
}

# The firmware binary should be created before generating the capsule
IMAGE_TYPEDEP:uefi_capsule:append = "${CAPSULE_IMGTYPE}"
