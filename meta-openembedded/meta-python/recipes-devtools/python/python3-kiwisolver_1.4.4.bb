SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f137eeae9cf8004d06830f6ab25b2d52"

SRC_URI[sha256sum] = "d41997519fcba4a1e46eb4a2fe31bc12f0ff957b2b81bac28db24744f333e955"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
