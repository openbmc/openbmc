SUMMARY = "Backport of functools.lru_cache from Python 3.3"
HOMEPAGE = "https://github.com/jaraco/backports.functools_lru_cache"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=57;endline=57;md5=98fc3658e5970d26f6b2109808c766be"

PYPI_PACKAGE = "backports.functools_lru_cache"

SRC_URI[md5sum] = "20f53f54cd3f04b3346ce75a54959754"
SRC_URI[sha256sum] = "9d98697f088eb1b0fa451391f91afb5e3ebde16bbdb272819fd091151fda4f1a"

DEPENDS += "python-setuptools-scm-native"

inherit setuptools pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
