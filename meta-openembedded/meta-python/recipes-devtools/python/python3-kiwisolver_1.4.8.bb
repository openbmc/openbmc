SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f7ce5ba663b186ce35b78df96a2eb0a"

SRC_URI[sha256sum] = "23d5f023bdc8c7e54eb65f03ca5d5bb25b601eac4d7f1a042888a1f45237987e"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
