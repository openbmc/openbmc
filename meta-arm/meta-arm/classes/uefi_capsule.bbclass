# This class generates UEFI capsules
# The current class supports generating a capsule with single firmware binary

DEPENDS += "gettext-native"
inherit python3native

IMAGE_TYPES += "uefi_capsule"

# edk2 base tools should be installed in the native sysroot directory
do_image_uefi_capsule[depends] += "edk2-basetools-native:do_populate_sysroot"

# By default the wic image is used to create a capsule
CAPSULE_IMGTYPE ?= "wic"

# IMGDEPLOYDIR is used as the default location of firmware binary for which the capsule needs to be created
CAPSULE_IMGLOCATION ?= "${IMGDEPLOYDIR}"

# The generated capsule by default has uefi.capsule extension
CAPSULE_EXTENSION ?= "uefi.capsule"

# The following variables must be set to be able to generate a capsule update
UEFI_FIRMWARE_BINARY ?= ""
UEFI_CAPSULE_CONFIG ?= ""

# Check if the required variables are set
python() {
    for var in ["UEFI_FIRMWARE_BINARY", "UEFI_CAPSULE_CONFIG"]:
        if not d.getVar(var):
            raise bb.parse.SkipRecipe(f"{var} not set")
}

IMAGE_CMD:uefi_capsule(){

    # Force the GenerateCapsule script to use python3
    export PYTHON_COMMAND=${PYTHON}

    # Copy the firmware and the capsule config json to current directory
    if [ -e ${CAPSULE_IMGLOCATION}/${UEFI_FIRMWARE_BINARY} ]; then
        cp ${CAPSULE_IMGLOCATION}/${UEFI_FIRMWARE_BINARY} . ;
    fi

    export UEFI_FIRMWARE_BINARY=${UEFI_FIRMWARE_BINARY}
    envsubst < ${UEFI_CAPSULE_CONFIG} > ./${MACHINE}-capsule-update-image.json

    ${STAGING_DIR_NATIVE}/usr/bin/edk2-BaseTools/BinWrappers/PosixLike/GenerateCapsule \
    -e -o ${IMGDEPLOYDIR}/${UEFI_FIRMWARE_BINARY}.${CAPSULE_EXTENSION} -j \
    ${MACHINE}-capsule-update-image.json

    # Remove the firmware to avoid contamination of IMGDEPLOYDIR
    rm ${UEFI_FIRMWARE_BINARY}

}

# The firmware binary should be created before generating the capsule
IMAGE_TYPEDEP:uefi_capsule:append = "${CAPSULE_IMGTYPE}"
