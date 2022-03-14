# ASPEED AST2600 devices can use Aspeed's utility 'otptool'
# to create OTP image
# The variables below carry default values to the create_otp()
# function below.
OTPTOOL_CONFIG ?= ""
OTPTOOL_KEY_DIR ?= ""
OTPTOOL_EXTRA_OPTS ?= ""
OTPTOOL_EXTRA_DEPENDS ?= " socsec-native"
DEPENDS += '${@oe.utils.conditional("SOCSEC_SIGN_ENABLE", "1", "${OTPTOOL_EXTRA_DEPENDS}", "", d)}'

# Creates the OTP image
create_otp_helper() {
    if [ "${SOC_FAMILY}" != "aspeed-g6" ] ; then
        bbwarn "OTP creation is only supported on AST2600 boards"
    elif [ ! -e "${OTPTOOL_CONFIG}" ] ; then
        bbfatal "Invalid otptool config: ${OTPTOOL_CONFIG}"
    elif [ ! -d "${OTPTOOL_KEY_DIR}" ] ; then
        bbfatal "Invalid otptool signing key directory: ${OTPTOOL_KEY_DIR}"
    else
        otptool make_otp_image \
            --key_folder ${OTPTOOL_KEY_DIR} \
            ${OTPTOOL_CONFIG} \
            ${OTPTOOL_EXTRA_OPTS}

        if [ $? -ne 0 ]; then
            bbfatal "Generated OTP image failed."
        fi

        otptool \
            print \
            ${B}/${CONFIG_B_PATH}/otp-all.image

        if [ $? -ne 0 ]; then
            bbfatal "Printed OTP image failed."
        fi

        install -m 0644 ${B}/${CONFIG_B_PATH}/otp-* ${DEPLOYDIR}
    fi
}

create_otp() {
    mkdir -p ${DEPLOYDIR}
    if [ -n "${UBOOT_CONFIG}" ]; then
        for config in ${UBOOT_MACHINE}; do
            CONFIG_B_PATH="${config}"
            cd ${B}/${config}
            create_otp_helper
        done
    else
        CONFIG_B_PATH=""
        cd ${B}
        create_otp_helper
    fi
}

do_deploy:prepend() {
    if [ "${SOCSEC_SIGN_ENABLE}" = "1" ] ; then
        create_otp
    fi
}
