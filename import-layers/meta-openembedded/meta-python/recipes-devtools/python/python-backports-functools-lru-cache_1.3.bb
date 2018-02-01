SUMMARY = "Backport of functools.lru_cache from Python 3.3"
HOMEPAGE = "https://github.com/jaraco/backports.functools_lru_cache"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=42;endline=42;md5=98fc3658e5970d26f6b2109808c766be"

PYPI_PACKAGE = "backports.functools_lru_cache"

SRC_URI[md5sum] = "b26a223250bd24ea2e0ad6ce47b19084"
SRC_URI[sha256sum] = "444a21bcec4ae177da554321f81a78dc879eaa8f6ea9920cb904830585d31e95"

DEPENDS = "python-setuptools-scm-native"

inherit setuptools pypi
