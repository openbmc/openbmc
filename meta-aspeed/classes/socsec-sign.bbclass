# ASPEED AST2600 devices can use Aspeed's utility 'socsec'
# to sign the SPL (pubkey written to OTP region)
# The variables below carry default values to the spl_sign()
# function below.
SOCSEC_SIGN_ENABLE ?= "0"
SOCSEC_SIGN_KEY ?= ""
SOCSEC_SIGN_SOC ?= "2600"
SOCSEC_SIGN_ALGO ?= "RSA4096_SHA512"
SOCSEC_SIGN_HELPER ?= ""
# u-boot-aspeed-sdk commit '2c3b53489c ast2600: Modify SPL SRAM layout'
# changes the SDRAM layout so that the verification region does NOT
# intersects the stack. The parameter below can be used to instruct
# socsec to work in either mode (ommitting it throws a warning), but
# newer (post v00.03.03) u-boot-aspeed-sdk need this set to false
SOCSEC_SIGN_EXTRA_OPTS ?= "--stack_intersects_verification_region=false"
DEPENDS += '${@oe.utils.conditional("SOCSEC_SIGN_ENABLE", "1", " socsec-native", "", d)}'


# Signs the SPL binary with a pre-established key
sign_spl_helper() {
    signing_helper_args=""

    if [ "${SOC_FAMILY}" != "aspeed-g6" ] ; then
        echo "Warning: SPL signing is only supported on AST2600 boards"
    elif [ ! -e "${SOCSEC_SIGN_KEY}" ] ; then
        echo "Warning: Invalid socsec signing key - SPL verified boot won't be available"
    else
        rm -f ${SPL_BINARY}.staged

        if [ -n "${SOCSEC_SIGN_HELPER}" ] ; then
            signing_helper_args="--signing_helper ${SOCSEC_SIGN_HELPER}"
        fi
        socsec make_secure_bl1_image \
            --soc ${SOCSEC_SIGN_SOC}  \
            --algorithm ${SOCSEC_SIGN_ALGO} \
            --rsa_sign_key ${SOCSEC_SIGN_KEY} \
            --bl1_image ${DEPLOYDIR}/${SPL_IMAGE} \
            ${signing_helper_args} \
            ${SOCSEC_SIGN_EXTRA_OPTS} \
            --output ${SPL_BINARY}.staged
        cp -f ${SPL_BINARY}.staged ${B}/${CONFIG_B_PATH}/${SPL_BINARY}
        mv -f ${SPL_BINARY}.staged ${DEPLOYDIR}/${SPL_IMAGE}
    fi
}

sign_spl() {
    mkdir -p ${DEPLOYDIR}
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            CONFIG_B_PATH="${config}"
            cd ${B}/${config}
            sign_spl_helper
        done
    else
        CONFIG_B_PATH=""
        cd ${B}
        sign_spl_helper
    fi
}


do_deploy_append() {
    if [ "${SOCSEC_SIGN_ENABLE}" = "1" -a -n "${SPL_BINARY}" ] ; then
        sign_spl
    fi
}
