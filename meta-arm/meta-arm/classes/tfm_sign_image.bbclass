# Functionality to sign binary images using the wrapper script bundled with
# TF-M. Signed images are written to the deploy directory by default.
# To use:
#  * Inherit this class
#  * Override the do_sign_images task
#  * Write the signing logic, which may call the function sign_host_image,
#    described below

inherit python3native

# The output and working directory
TFM_IMAGE_SIGN_DIR = "${WORKDIR}/tfm-signed-images"
TFM_IMAGE_SIGN_DEPLOY_DIR = "${WORKDIR}/deploy-tfm-signed-images"

SSTATETASKS += "do_sign_images"
do_sign_images[sstate-inputdirs] = "${TFM_IMAGE_SIGN_DEPLOY_DIR}"
do_sign_images[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_sign_images[dirs] = "${TFM_IMAGE_SIGN_DEPLOY_DIR} ${TFM_IMAGE_SIGN_DIR}"
do_sign_images[cleandirs] = "${TFM_IMAGE_SIGN_DEPLOY_DIR} ${TFM_IMAGE_SIGN_DIR}"
do_sign_images[stamp-extra-info] = "${MACHINE_ARCH}"
tfm_sign_image_do_sign_images() {
    :
}
addtask sign_images after do_prepare_recipe_sysroot before do_image
EXPORT_FUNCTIONS do_sign_images

python do_sign_images_setscene () {
    sstate_setscene(d)
}
addtask do_sign_images_setscene

DEPENDS += "trusted-firmware-m-scripts-native"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

# The arguments passed to the TF-M image signing script. Override this variable
# in an image recipe to customize the arguments.
TFM_IMAGE_SIGN_ARGS ?= "\
    -v ${RE_LAYOUT_WRAPPER_VERSION} \
    --layout "${TFM_IMAGE_SIGN_DIR}/${host_binary_layout}" \
    -k  "${RECIPE_SYSROOT_NATIVE}/${TFM_SIGN_PRIVATE_KEY}" \
    --public-key-format full \
    --align 1 \
    --pad \
    --pad-header \
    --measured-boot-record \
    -H ${RE_IMAGE_OFFSET} \
    -s auto \
"

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

    host_binary_signed="${TFM_IMAGE_SIGN_DEPLOY_DIR}/signed_$(basename "${1}")"

    ${PYTHON} "${STAGING_LIBDIR_NATIVE}/tfm-scripts/wrapper/wrapper.py" \
            ${TFM_IMAGE_SIGN_ARGS} \
            "${1}" \
            "${host_binary_signed}"
}
