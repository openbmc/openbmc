SUMMARY = "Expression parser"
HOMEPAGE = "https://github.com/ArashPartow/exprtk"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRCREV = "281c2ccc65b8f91c012ea3725ebcef406378a225"

SRC_URI = "git://github.com/ArashPartow/exprtk.git"

S = "${WORKDIR}/git"

# other packages commonly reference the file directly as "exprtk.hpp"
# create symlink to allow this usage
do_install() {
    install -d ${D}/${includedir}
    install -m 0644 ${S}/exprtk.hpp ${D}/${includedir}/exprtk.hpp
}

# exprtk is a header only C++ library, so the main package will be empty.
RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
