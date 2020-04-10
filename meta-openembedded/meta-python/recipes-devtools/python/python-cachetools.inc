SUMMARY = "Extensible memoizing collections and decorators"
HOMEPAGE = "https://github.com/tkem/cachetools"
DESCRIPTION = "This module provides various memoizing \
collections and decorators, including variants of the \
Python 3 Standard Library @lru_cache function decorator."
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27f7518eb6f7dc686d0f953b2f28dae5"

inherit pypi

SRC_URI[md5sum] = "6a88df13467e80eb61dd2bedad19b83c"
SRC_URI[sha256sum] = "9a52dd97a85f257f4e4127f15818e71a0c7899f121b34591fcc1173ea79a0198"

BBCLASSEXTEND = "native nativesdk"
