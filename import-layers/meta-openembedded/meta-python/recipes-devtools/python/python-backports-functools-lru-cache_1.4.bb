SUMMARY = "Backport of functools.lru_cache from Python 3.3"
HOMEPAGE = "https://github.com/jaraco/backports.functools_lru_cache"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=47;endline=47;md5=98fc3658e5970d26f6b2109808c766be"

PYPI_PACKAGE = "backports.functools_lru_cache"

SRC_URI[md5sum] = "b954e7d5e2ca0f0f66ad2ed12ba800e5"
SRC_URI[sha256sum] = "31f235852f88edc1558d428d890663c49eb4514ffec9f3650e7f3c9e4a12e36f"

DEPENDS += "python-setuptools-scm-native"

inherit setuptools pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
