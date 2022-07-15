SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f137eeae9cf8004d06830f6ab25b2d52"

SRC_URI[sha256sum] = "ab8a15c2750ae8d53e31f77a94f846d0a00772240f1c12817411fa2344351f86"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
