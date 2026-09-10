SUMMARY = "Extensible memoizing collections and decorators"
HOMEPAGE = "https://github.com/tkem/cachetools"
DESCRIPTION = "This module provides various memoizing \
collections and decorators, including variants of the \
Python 3 Standard Library @lru_cache function decorator."
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=079933dfba36eb60b5e3512ca0ab61ae"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += " \
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	python3-math \
"

SRC_URI[sha256sum] = "a3e2a00b14d8f8a6b70c1dae7b4685e7ad3bc965c5b42124a2d6ce895da6cf50"

BBCLASSEXTEND = "native nativesdk"
