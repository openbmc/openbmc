# Functionality to sign binary images using the wrapper script bundled with
# TF-M. Signed images are written to the deploy directory by default.
# To use:
#  * Inherit this class
#  * Override the do_sign_images task
#  * Write the signing logic, which may call the function sign_host_image,
#    described below

inherit python3native deploy

# The output and working directory
TFM_IMAGE_SIGN_DIR = "${WORKDIR}/tfm-signed-images"

tfm_sign_image_do_sign_images() {
    :
}
addtask sign_images after do_configure before do_compile
do_sign_images[dirs] = "${TFM_IMAGE_SIGN_DIR}"

tfm_sign_image_do_deploy() {
    :
}
addtask deploy after do_sign_images

deploy_signed_images() {
    cp ${TFM_IMAGE_SIGN_DIR}/signed_* ${DEPLOYDIR}/
}
do_deploy[postfuncs] += "deploy_signed_images"

EXPORT_FUNCTIONS do_sign_images do_deploy

DEPENDS += "trusted-firmware-m-scripts-native"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

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
# $1 ... path of binary to sign
# $2 ... load address of the given binary
# $3 ... signed binary size
#
# Note: The signed binary is copied to ${TFM_IMAGE_SIGN_DIR}
#
sign_host_image() {
    host_binary_filename="$(basename -s .bin "${1}")"
    host_binary_layout="${host_binary_filename}_ns"

    cat << EOF > ${TFM_IMAGE_SIGN_DIR}/${host_binary_layout}
enum image_attributes {
    RE_IMAGE_LOAD_ADDRESS = ${2},
    RE_SIGN_BIN_SIZE = ${3},
};
EOF

    host_binary_signed="${TFM_IMAGE_SIGN_DIR}/signed_$(basename "${1}")"

    ${PYTHON} "${STAGING_LIBDIR_NATIVE}/tfm-scripts/wrapper/wrapper.py" \
            -v ${RE_LAYOUT_WRAPPER_VERSION} \
            --layout "${TFM_IMAGE_SIGN_DIR}/${host_binary_layout}" \
            -k  "${RECIPE_SYSROOT_NATIVE}/${TFM_SIGN_PRIVATE_KEY}" \
            --public-key-format full \
            --align 1 \
            --pad \
            --pad-header \
            -H ${RE_IMAGE_OFFSET} \
            -s auto \
            "${1}" \
            "${host_binary_signed}"
}
