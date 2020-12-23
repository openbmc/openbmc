require u-boot-common-aspeed-sdk_${PV}.inc

UBOOT_MAKE_TARGET ?= "DEVICE_TREE=${UBOOT_DEVICETREE}"

require u-boot-aspeed.inc

PROVIDES += "u-boot"
DEPENDS += "bc-native dtc-native"

SRC_URI_append_df-phosphor-mmc = " file://u-boot-env-ast2600.txt"

UBOOT_ENV_SIZE_df-phosphor-mmc = "0x10000"
UBOOT_ENV_df-phosphor-mmc = "u-boot-env"
UBOOT_ENV_SUFFIX_df-phosphor-mmc = "bin"

do_compile_append() {
    if [ -n "${UBOOT_ENV}" ]
    then
        # Generate redundant environment image
        ${B}/tools/mkenvimage -r -s ${UBOOT_ENV_SIZE} -o ${WORKDIR}/${UBOOT_ENV_BINARY} ${WORKDIR}/u-boot-env-ast2600.txt
    fi
}
