SUMMARY = "Pure Python 7-zip library"
HOMEPAGE = "https://py7zr.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "2aee212c5516ddcbeb76874dc3ece38b4566fc003f51600032c723cfea89ac56"

CVE_PRODUCT = "py7zr"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
    python3-coherent-licensed-native \
"

RDEPENDS:${PN} += "\
    python3-pycryptodomex \
    python3-multivolumefile \
    python3-pybcj \
    python3-inflate64 \
    python3-pyppmd \
    python3-pyzstd \
    python3-brotli \
    python3-multiprocessing \
    python3-datetime \
    python3-core \
    python3-threading \
"
