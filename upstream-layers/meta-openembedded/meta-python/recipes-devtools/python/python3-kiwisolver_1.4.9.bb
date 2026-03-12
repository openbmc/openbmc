SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27a71439e89b80e41dc526a4b8bbfa2f"

SRC_URI[sha256sum] = "c3b22c26c6fd6811b0ae8363b95ca8ce4ea3c202d3d0975b2914310ceb1bcc4d"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
