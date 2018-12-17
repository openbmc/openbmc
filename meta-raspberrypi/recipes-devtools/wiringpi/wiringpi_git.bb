DESCRIPTION = "A library to control Raspberry Pi GPIO channels"
HOMEPAGE = "https://projects.drogon.net/raspberry-pi/wiringpi/"
SECTION = "devel/libs"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "virtual/crypt"

# tag 2.46
SRCREV = "8d188fa0e00bb8c6ff6eddd07bf92857e9bd533a"

S = "${WORKDIR}/git"

SRC_URI = "git://git.drogon.net/wiringPi \
           file://0001-Add-initial-cross-compile-support.patch \
           "

COMPATIBLE_MACHINE = "^rpi$"

CFLAGS_prepend = "-I${S}/wiringPi -I${S}/devLib "

EXTRA_OEMAKE += "'INCLUDE_DIR=${D}${includedir}' 'LIB_DIR=${D}${libdir}'"
EXTRA_OEMAKE += "'DESTDIR=${D}/usr' 'PREFIX=""'"

do_compile() {
    oe_runmake -C devLib
    oe_runmake -C wiringPi
    oe_runmake -C gpio 'LDFLAGS=${LDFLAGS} -L${S}/wiringPi -L${S}/devLib'
}

do_install() {
    oe_runmake -C devLib install
    oe_runmake -C wiringPi install
    oe_runmake -C gpio install
}
