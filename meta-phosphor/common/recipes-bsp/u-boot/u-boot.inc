LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRCREV = "a5e0a21ed4b62ab4d8ff09f70b751db0f46ddaac"
UBRANCH = "v2016.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-filesystem', \
    'file://0001-config-ast-common-hack-bootopts.patch \
     file://0002-config-ast-g5-add-ubi-support.patch \
     file://0003-Add-MTD-support-to-Aspeed-Flash-driver.patch \
     file://0004-config-ast-g5-add-CONFIG_FLASH_CFI_MTD.patch', '', d)}"

PV = "v2016.07+git${SRCPV}"
