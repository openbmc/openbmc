SUMARY = "Corstone1000 platform Image"
DESCRIPTION = "This is the main image which is the container of all the binaries \
               generated for the Corstone1000 platform."
LICENSE = "MIT"

COMPATIBLE_MACHINE = "corstone1000"

inherit image
inherit wic_nopt tfm_sign_image

PACKAGE_INSTALL = ""

IMAGE_FSTYPES += "wic wic.nopt"

do_sign_images() {
    # Sign TF-A BL2
    sign_host_image ${RECIPE_SYSROOT}/firmware/${TFA_BL2_BINARY} \
        ${TFA_BL2_RE_IMAGE_LOAD_ADDRESS} ${TFA_BL2_RE_SIGN_BIN_SIZE}

    # Update BL2 in the FIP image
    cp ${RECIPE_SYSROOT}/firmware/${TFA_FIP_BINARY} .
    fiptool update --tb-fw ${TFM_IMAGE_SIGN_DIR}/signed_${TFA_BL2_BINARY} \
        ${TFM_IMAGE_SIGN_DIR}/${TFA_FIP_BINARY}

    # Sign the FIP image
    sign_host_image ${TFM_IMAGE_SIGN_DIR}/${TFA_FIP_BINARY} \
        ${TFA_FIP_RE_IMAGE_LOAD_ADDRESS} ${TFA_FIP_RE_SIGN_BIN_SIZE}
}
do_sign_images[depends] = "\
    trusted-firmware-a:do_populate_sysroot \
    fiptool-native:do_populate_sysroot \
    "
