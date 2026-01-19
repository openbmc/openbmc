require u-boot-axiado.inc

SRCREV ?= "aa4751c2b02c5e54d8911a6755c5b55cd10d893a"

SRC_URI = "git://github.com/axiado/u-boot-axiado;protocol=https;branch=u-boot-2019.04-axiado"
SRC_URI += "file://u-boot-axiado-env"

# 2019.04 does not have u-boot-initial-env target to build
UBOOT_INITIAL_ENV = ""

do_compile:append() {
    ${B}/tools/mkenvimage -s ${UBOOT_ENV_SIZE} -o ${B}/${UBOOT_ENV_BINARY} ${UNPACKDIR}/u-boot-axiado-env
}

PV = "2019.04+git${SRCPV}"
