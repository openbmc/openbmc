require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DEPENDS += "bc-native dtc-native gnutls-native python3-pyelftools-native"

PROVIDES += "u-boot"

# Install the default environment file expected by libubootenv.
UBOOT_INITIAL_ENV = "u-boot-initial-env"

UBRANCH = "v2026.10-openbmc"
SRC_URI = "git://github.com/openbmc/u-boot.git;protocol=https;branch=${UBRANCH}"
SRCREV = "196cee41b40231fed472b52373ce1213d74287f3"

UBOOT_ENV:df-phosphor-mmc = "u-boot-env"
UBOOT_ENV_SUFFIX:df-phosphor-mmc = "bin"
UBOOT_ENV_SRC:df-phosphor-mmc = "${UBOOT_ENV_TXT}"

SRC_URI:append:df-phosphor-mmc:aspeed-g7 = " file://${UBOOT_ENV_TXT}"
UBOOT_ENV_SIZE:df-phosphor-mmc:aspeed-g7 = "0x20000"
UBOOT_ENV_TXT:df-phosphor-mmc:aspeed-g7 = "${@bb.utils.contains('MACHINE_FEATURES', 'mf-ufs', \
                'u-boot-env-ast2700-ufs.txt', 'u-boot-env-ast2700.txt', d)}"

do_compile:append() {
    if [ -n "${UBOOT_ENV}" ]
    then
        # Generate default environment image
        # add -r parameter if wants redundant environment image
        ${B}/tools/mkenvimage -s ${UBOOT_ENV_SIZE} -o ${B}/${UBOOT_ENV_BINARY} ${UNPACKDIR}/${UBOOT_ENV_TXT}
    fi
}
