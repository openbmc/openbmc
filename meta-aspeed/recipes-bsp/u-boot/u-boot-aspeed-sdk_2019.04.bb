require u-boot-common-aspeed-sdk_${PV}.inc

UBOOT_MAKE_TARGET ?= "DEVICE_TREE=${UBOOT_DEVICETREE}"

require u-boot-aspeed.inc
inherit socsec-sign

PROVIDES += "u-boot"
DEPENDS += "bc-native dtc-native"

SRC_URI_append_df-phosphor-mmc = " file://u-boot-env-ast2600.txt"
SRC_URI += " \
            file://rsa_oem_dss_key.pem;sha256sum=64a379979200d39949d3e5b0038e3fdd5548600b2f7077a17e35422336075ad4 \
            file://rsa_pub_oem_dss_key.pem;sha256sum=40132a694a10af2d1b094b1cb5adab4d6b4db2a35e02d848b2b6a85e60738264 \
           "

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
