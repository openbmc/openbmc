SUMMARY = "Expression parser"
HOMEPAGE = "https://www.partow.net/programming/exprtk/index.html"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRCREV = "a4b17d543f072d2e3ba564e4bc5c3a0d2b05c338"

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
