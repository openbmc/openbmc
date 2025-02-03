SUMMARY = "Extensible memoizing collections and decorators"
HOMEPAGE = "https://github.com/tkem/cachetools"
DESCRIPTION = "This module provides various memoizing \
collections and decorators, including variants of the \
Python 3 Standard Library @lru_cache function decorator."
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=539275e657c6b7af026bb908356f7541"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
	python3-math \
"

SRC_URI[sha256sum] = "70f238fbba50383ef62e55c6aff6d9673175fe59f7c6782c7a0b9e38f4a9df95"

BBCLASSEXTEND = "native nativesdk"
