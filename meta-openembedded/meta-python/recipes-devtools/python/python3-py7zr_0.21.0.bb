SUMMARY = "Pure Python 7-zip library"
HOMEPAGE = "https://py7zr.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "213a9cc46940fb8f63b4163643a8f5b36bbc798134746c3992d3bc6b14edab87"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
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
