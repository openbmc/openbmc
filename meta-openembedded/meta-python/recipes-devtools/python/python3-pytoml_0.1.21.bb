SUMMARY = "A TOML-0.4.0 parser/writer for Python"
HOMEPAGE = "https://pypi.python.org/pypi/pytoml/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfff423699bdaef24331933ac4f56078"

SRC_URI[sha256sum] = "8eecf7c8d0adcff3b375b09fe403407aa9b645c499e5ab8cac670ac4a35f61e7"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "test"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-stringold \
    "

