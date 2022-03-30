SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f137eeae9cf8004d06830f6ab25b2d52"

SRC_URI[sha256sum] = "7508b01e211178a85d21f1f87029846b77b2404a4c68cbd14748d4d4142fa3b8"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
