DESCRIPTION = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f8a8f918f1513404c8366d7a63ab6d97"

SRC_URI = "git://github.com/nlohmann/json.git;branch=release/3.0.0"

PV = "3.0.0+git${SRCPV}"

SRCREV = "106f9f5436f6726006627ce3122fddf7fc9ca330"

S = "${WORKDIR}/git"
do_install_append(){
    install -d ${D}${includedir}/nlohmann
    install -m 644 ${S}/src/json.hpp ${D}${includedir}/nlohmann/json.hpp
}
