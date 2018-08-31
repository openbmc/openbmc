DESCRIPTION = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=9a8ae1c2d606c432a2aa2e2de15be22a"

SRC_URI = "git://github.com/nlohmann/json.git"

ALLOW_EMPTY_${PN} = "1"

PV = "3.1.2+git${SRCPV}"

SRCREV = "183390c10b8ba4aa33934ae593f82f352befefc8"

S = "${WORKDIR}/git"
do_install_append(){
    install -d ${D}${includedir}/nlohmann
    install -m 644 ${S}/single_include/nlohmann/json.hpp ${D}${includedir}/nlohmann/json.hpp
}
