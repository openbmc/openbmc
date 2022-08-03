# Signing host images using TF-M tools

DEPENDS += "python3-imgtool-native fiptool-native"

#
# sign_host_image
#
# Description:
#
# A generic function that signs a host image
# using MCUBOOT format
#
# Arguments:
#
# $1 ... host binary to sign
# $2 ... host binary path
# $3 ... load address of the given binary
# $4 ... signed binary size
#
# Note: The signed binary is copied to ${D}/firmware
#
sign_host_image() {

    host_binary_filename="`basename -s .bin ${1}`"
    host_binary_layout="${host_binary_filename}_ns"

    cat << EOF > ${B}/${host_binary_layout}
enum image_attributes {
    RE_IMAGE_LOAD_ADDRESS = ${3},
    RE_SIGN_BIN_SIZE = ${4},
};
EOF

    host_binary="${2}/`basename ${1}`"
    host_binary_signed="${D}/firmware/signed_`basename ${1}`"

    ${PYTHON} ${S}/bl2/ext/mcuboot/scripts/wrapper/wrapper.py \
            -v ${RE_LAYOUT_WRAPPER_VERSION} \
            --layout ${B}/${host_binary_layout} \
            -k  ${TFM_SIGN_PRIVATE_KEY} \
            --public-key-format full \
            --align 1 \
            --pad \
            --pad-header \
            -H ${RE_IMAGE_OFFSET} \
            -s auto \
            ${host_binary} \
            ${host_binary_signed}

}
