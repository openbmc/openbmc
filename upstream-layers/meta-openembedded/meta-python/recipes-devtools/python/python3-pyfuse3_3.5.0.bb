SUMMARY = "Python bindings for libfuse3."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=622e3d340933e3857b7561f37a2f412b"

inherit pypi python_setuptools_build_meta python_pep517 cython pkgconfig

SRC_URI[sha256sum] = "88399a9494b88603230bba300f4ba9ad63fece5ed514ca3633d555a0c6a42b24"

DEPENDS = " \
    fuse3 \
    python3-setuptools-native \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
    python3-pickle \
    python3-threading \
    python3-trio \
"
