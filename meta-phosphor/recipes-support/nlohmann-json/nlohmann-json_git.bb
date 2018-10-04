SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=9a8ae1c2d606c432a2aa2e2de15be22a"

SRC_URI = "git://github.com/nlohmann/json.git"

PV = "3.2.0+git${SRCPV}"

SRCREV = "8c20571136f2d5351b379a06ad6591bd980880fe"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DJSON_BuildTests=OFF"

# nlohmann-json is a header only C++ library, so the main package will be empty.

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
