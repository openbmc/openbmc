require recipes-bsp/u-boot/u-boot.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
DEPENDS += "dtc-native"

SRCREV = "${AUTOREV}"
UBRANCH = "v2016.07-aspeed-openbmc"
SRC_URI = "git://github.com/mellanoxbmc/u-boot.git;branch=${UBRANCH};protocol=git; "

PV = "v2016.07+git${SRCPV}"

do_deploy_append () {
	if [ -e ${B}/u-boot-env.bin ] ; then
        install -m 644 ${B}/u-boot-env.bin ${DEPLOYDIR}
    fi
}