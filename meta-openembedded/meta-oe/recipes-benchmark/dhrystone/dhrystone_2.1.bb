SUMMARY = "Dhrystone CPU benchmark"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/PD;md5=b3597d12946881e13cb3b548d1173851"

SRC_URI = "http://www.netlib.org/benchmark/dhry-c;downloadfilename=dhry-c.shar \
           file://dhrystone.patch"
SRC_URI[md5sum] = "75aa5909c174eed98c134be2f56307da"
SRC_URI[sha256sum] = "038a7e9169787125c3451a6c941f3aca5db2d2f3863871afcdce154ef17f4e3e"

# Need to override Makefile variables
EXTRA_OEMAKE = "-e MAKEFLAGS="

do_unpack() {
    [ -d ${S} ] || mkdir -p ${S}
    cd ${S}
    sh ${DL_DIR}/dhry-c.shar
}
do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/dhry ${D}${bindir}
}

# Prevent procedure merging as required by dhrystone.c:
CFLAGS += "-fno-lto"
CFLAGS:append:toolchain-clang = " -Wno-error=implicit-function-declaration -Wno-error=deprecated-non-prototype -Wno-error=implicit-int"

LDFLAGS += "-fno-lto"
