# SPI uboot image should support eMMC
SRC_URI:append = " file://u-boot-emmc.cfg"
SRC_URI:append:df-phosphor-mmc = " file://u-boot-env-emmc.txt"

UBOOT_ENV_SIZE:df-phosphor-mmc = "0x40000"
UBOOT_ENV:df-phosphor-mmc = "u-boot-env"
UBOOT_ENV_SUFFIX:df-phosphor-mmc = "bin"
UBOOT_ENV_TXT:df-phosphor-mmc = "u-boot-env-emmc.txt"

do_compile:append() {
    if [ -n "${UBOOT_ENV}" ]
    then
        # Generate redundant environment image
        ${B}/tools/mkenvimage -s ${UBOOT_ENV_SIZE} -p 0 -o ${WORKDIR}/${UBOOT_ENV_BINARY} ${WORKDIR}/${UBOOT_ENV_TXT}
    fi
}
