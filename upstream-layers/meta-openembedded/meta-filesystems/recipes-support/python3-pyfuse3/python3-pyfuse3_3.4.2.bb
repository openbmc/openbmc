SUMMARY = "Python bindings for libfuse3."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=622e3d340933e3857b7561f37a2f412b"

inherit pypi python_setuptools_build_meta python_pep517 cython pkgconfig

SRC_URI[sha256sum] = "0a59031969c4ba51a5ec1b67f3c5c24f641a6a3f8119a86edad56debcb9084d9"

DEPENDS = " \
    fuse3 \
    python3-setuptools-native \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} = "python3-trio"
