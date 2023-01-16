# ASPEED AST2600 devices can use Aspeed's utility 'otptool'
# to create OTP image
# The variables below carry default values to the create_otp()
# function below.
OTPTOOL_CONFIGS ?= ""
OTPTOOL_KEY_DIR ?= ""
OTPTOOL_USER_DIR ?= ""
OTPTOOL_EXTRA_OPTS ?= ""
OTPTOOL_EXTRA_DEPENDS ?= " socsec-native"
DEPENDS += '${@oe.utils.conditional("SOCSEC_SIGN_ENABLE", "1", "${OTPTOOL_EXTRA_DEPENDS}", "", d)}'

do_otptool() {
    local otptool_config=$1
    otptool_config_slug="$(basename ${otptool_config} .json)"
    otptool_config_outdir="${B}"/"${CONFIG_B_PATH}"/"${otptool_config_slug}"
    otptool_user_folder="$([ -n "${OTPTOOL_USER_DIR}" ] && echo --user_data_folder ${OTPTOOL_USER_DIR})"
    mkdir -p "${otptool_config_outdir}"
    otptool make_otp_image \
        --key_folder ${OTPTOOL_KEY_DIR} \
        --output_folder "${otptool_config_outdir}" \
        ${otptool_user_folder} \
        ${otptool_config} \
        ${OTPTOOL_EXTRA_OPTS}

    if [ $? -ne 0 ]; then
        bbfatal "Generated OTP image failed."
    fi

    otptool print "${otptool_config_outdir}"/otp-all.image

    if [ $? -ne 0 ]; then
        bbfatal "Printed OTP image failed."
    fi

    install -m 0644 -T \
        "${otptool_config_outdir}"/otp-all.image \
        ${DEPLOYDIR}/"${otptool_config_slug}"-otp-all.image
}

# Creates the OTP image
create_otp_helper() {
    if [ "${SOC_FAMILY}" != "aspeed-g6" ] ; then
        bbwarn "OTP creation is only supported on AST2600 boards"
    elif [ -z "${OTPTOOL_CONFIGS}" ] ; then
        bbfatal "OTPTOOL_CONFIGS is empty, no otptool configurations available"
    elif [ ! -d "${OTPTOOL_KEY_DIR}" ] ; then
        bbfatal "Invalid otptool signing key directory: ${OTPTOOL_KEY_DIR}"
    else
        for otptool_config in ${OTPTOOL_CONFIGS} ; do
            if [ ! -e ${otptool_config} ] ; then
                bbfatal "Invalid otptool config: ${otptool_config}"
            fi

            do_otptool $otptool_config
        done
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
