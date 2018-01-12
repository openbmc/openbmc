DESCRIPTION = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f8a8f918f1513404c8366d7a63ab6d97"

SRC_URI = "git://github.com/nlohmann/json.git"

ALLOW_EMPTY_${PN} = "1"

PV = "3.0.1+git${SRCPV}"

SRCREV = "ce1dccf347faf6beb2cdf06b788c01cc24e4c6ce"

S = "${WORKDIR}/git"
do_install_append(){
    install -d ${D}${includedir}/nlohmann
    install -m 644 ${S}/src/json.hpp ${D}${includedir}/nlohmann/json.hpp
}
