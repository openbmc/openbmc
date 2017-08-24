LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRCREV = "1b3fb9515dcd280fca764b2521f152ca04d04701"
UBRANCH = "v2016.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"

SRC_URI += "file://0001-configs-ast-Add-redundnant-env.patch"

SRC_URI += "${@bb.utils.contains('MACHINE_FEATURES', 'obmc-ubi-fs', \
    'file://0001-config-ast-common-hack-bootopts.patch \
     file://0004-config-ast-common-ubi-bootops.patch', '', d)}"

PV = "v2016.07+git${SRCPV}"
