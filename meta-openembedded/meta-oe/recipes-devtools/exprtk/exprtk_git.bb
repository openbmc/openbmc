SUMMARY = "Expression parser"
HOMEPAGE = "https://github.com/ArashPartow/exprtk"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRCREV = "f46bffcd6966d38a09023fb37ba9335214c9b959"

SRC_URI = "git://github.com/ArashPartow/exprtk.git;branch=release;protocol=https"

S = "${WORKDIR}/git"

# other packages commonly reference the file directly as "exprtk.hpp"
# create symlink to allow this usage
do_install() {
    install -d ${D}/${includedir}
    install -m 0644 ${S}/exprtk.hpp ${D}/${includedir}/exprtk.hpp
}

# exprtk is a header only C++ library, so the main package will be empty.
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
