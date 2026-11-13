# This class generates UEFI capsules
# The current class supports generating a capsule with multiple firmware binaries

IMAGE_TYPES += "uefi_capsule"

# edk2-basetools should be installed in the native sysroot directory
do_image_uefi_capsule[depends] += "edk2-basetools-native:do_populate_sysroot"

# By default the wic image is used to create a capsule
CAPSULE_IMGTYPE ?= "wic"

# IMGDEPLOYDIR is used as the default location of firmware binary for which the capsule needs to be created
CAPSULE_IMG_LOCATION ?= "${IMGDEPLOYDIR}"

# The generated capsule by default has uefi.capsule extension
CAPSULE_EXTENSION ?= "uefi.capsule"

# The generated capsule's name by default is the same as UEFI_FIRMWARE_BINARY
CAPSULE_NAME ??= "${UEFI_FIRMWARE_BINARY}"

# The generated capsule configuration file extension
CAPSULE_CONFIG_FILE_EXTENSION ?= "json"

# The generated capsule configuration file
CAPSULE_CONFIG_FILE ?= "${IMGDEPLOYDIR}/${CAPSULE_NAME}.${CAPSULE_CONFIG_FILE_EXTENSION}"

# Path to the script that generates the UEFI capsule payloads JSON
UEFI_CAPSULE_CONFIG_GENERATOR_SCRIPT ?= "${META_ARM_LAYER_DIR}/scripts/generate_capsule_json_multiple.py"

# Additional variables for capsule component filtering
CAPSULE_ALL_COMPONENTS ?= ""
CAPSULE_SELECTED_COMPONENTS ??= ""

# Variables required by the EDK2 GenerateCapsule tool.
CAPSULE_CERTIFICATE_PATHS ?= ""
CAPSULE_FW_VERSIONS ?= ""
CAPSULE_GUIDS ?= ""
CAPSULE_INDEXES ?= ""
CAPSULE_HARDWARE_INSTANCES ?= ""
CAPSULE_LOWEST_SUPPORTED_VERSIONS ?= ""
CAPSULE_MONOTONIC_COUNTS ?= ""
CAPSULE_PRIVATE_KEY_PATHS ?= ""
UEFI_FIRMWARE_BINARIES ?= ""
PAYLOAD_CERTIFICATE_PATH ?= ""
PAYLOAD_PRIVATE_KEY_PATH ?= ""


# Check if the required variables are set
python() {
    for var in ["CAPSULE_CERTIFICATE_PATHS", "CAPSULE_FW_VERSIONS", \
                "CAPSULE_GUIDS", "CAPSULE_INDEXES", \
                "CAPSULE_HARDWARE_INSTANCES", \
                "CAPSULE_LOWEST_SUPPORTED_VERSIONS", \
                "CAPSULE_MONOTONIC_COUNTS", "CAPSULE_PRIVATE_KEY_PATHS", \
                "UEFI_FIRMWARE_BINARIES", \
                "UEFI_CAPSULE_CONFIG_GENERATOR_SCRIPT", \
                "CAPSULE_ALL_COMPONENTS", \
                "CAPSULE_SELECTED_COMPONENTS"]:
        if not d.getVar(var):
            raise bb.parse.SkipRecipe(f"{var} not set")
}

IMAGE_CMD:uefi_capsule(){
    signing_pem_dir="${WORKDIR}/uefi-capsule-signing-pems"
    rm -rf "$signing_pem_dir"
    install -d -m 0700 "$signing_pem_dir"

    set -- ${CAPSULE_CERTIFICATE_PATHS}
    signing_pem_paths=""
    signing_pem_index=0
    for private_key_path in ${CAPSULE_PRIVATE_KEY_PATHS}; do
        if [ "$#" -eq 0 ]; then
            bbfatal "CAPSULE_PRIVATE_KEY_PATHS has more entries than CAPSULE_CERTIFICATE_PATHS"
        fi

        certificate_path="$1"
        shift

        signing_pem_path="$signing_pem_dir/$signing_pem_index.pem"
        install -m 0600 "$private_key_path" "$signing_pem_path"
        cat "$certificate_path" >> "$signing_pem_path"

        signing_pem_paths="$signing_pem_paths $signing_pem_path"
        signing_pem_index="$(expr "$signing_pem_index" + 1)"
    done

    if [ "$#" -ne 0 ]; then
        bbfatal "CAPSULE_CERTIFICATE_PATHS has more entries than CAPSULE_PRIVATE_KEY_PATHS"
    fi

    # Generates the UEFI capsule payloads JSON
    ${PYTHON} ${UEFI_CAPSULE_CONFIG_GENERATOR_SCRIPT} \
             --selected_components ${CAPSULE_SELECTED_COMPONENTS}\
             --components ${CAPSULE_ALL_COMPONENTS}\
             --fw_versions ${CAPSULE_FW_VERSIONS} \
             --guids ${CAPSULE_GUIDS} \
             --hardware_instances ${CAPSULE_HARDWARE_INSTANCES} \
             --lowest_supported_versions ${CAPSULE_LOWEST_SUPPORTED_VERSIONS} \
             --monotonic_counts ${CAPSULE_MONOTONIC_COUNTS} \
             --payloads ${UEFI_FIRMWARE_BINARIES} \
             --update_image_indexes ${CAPSULE_INDEXES} \
             --signing_pems $signing_pem_paths \
             --certificates ${CAPSULE_CERTIFICATE_PATHS} \
             --output ${CAPSULE_CONFIG_FILE}

    # Force the GenerateCapsule script to use python3
    export PYTHON_COMMAND=${PYTHON}

    # Generate the UEFI capsule image using the EDK2 GenerateCapsule tool
    ${STAGING_BINDIR_NATIVE}/edk2-BaseTools/BinWrappers/PosixLike/GenerateCapsule \
             -e -j ${CAPSULE_CONFIG_FILE} \
             ${CAPSULE_EXTRA_ARGS} \
             -o ${CAPSULE_IMG_LOCATION}/${CAPSULE_NAME}.${CAPSULE_EXTENSION}
}

# The firmware binary should be created before generating the capsule
IMAGE_TYPEDEP:uefi_capsule:append = "${CAPSULE_IMGTYPE}"
