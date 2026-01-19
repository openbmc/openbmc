require u-boot-axiado.inc

SRCREV ?= "52c41f051b5e01d2faffb6b7af12cfbca863feab"

do_compile:append() {
    ${B}/tools/mkenvimage -s ${UBOOT_ENV_SIZE} -o ${B}/${UBOOT_ENV_BINARY} ${B}/u-boot-initial-env
}

PV = "2022.01+git${SRCPV}"
