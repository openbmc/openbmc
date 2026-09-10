SUMMARY = "Pure Python 7-zip library"
HOMEPAGE = "https://py7zr.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/miurahr/py7zr.git;branch=master;protocol=https"
SRCREV = "e278bc05cc937ecd1ee62bc6a058db3a21e66614"

CVE_PRODUCT = "py7zr"

inherit python_setuptools_build_meta ptest-python-pytest

DEPENDS += "\
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
    python3-brotli \
    python3-multiprocessing \
    python3-datetime \
    python3-core \
    python3-threading \
"

RDEPENDS:${PN}-ptest += "\
    python3-texttable \
    python3-requests \
    python3-pytest-httpserver \
    python3-psutil \
"

do_install_ptest:append() {
    # Remove liblzma pre-stripped binaries because they are not
    # neeed and to avoid the already-stripped QA errors
    rm -rf ${D}${PTEST_PATH}/tests/data/lib
}
