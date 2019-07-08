SUMMARY = "Extensible memoizing collections and decorators"
HOMEPAGE = "https://github.com/tkem/cachetools"
DESCRIPTION = "This module provides various memoizing \
collections and decorators, including variants of the \
Python 3 Standard Library @lru_cache function decorator."
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27f7518eb6f7dc686d0f953b2f28dae5"

inherit pypi

SRC_URI[md5sum] = "a7b1c5eb5390d50067c62d7bcb4e875c"
SRC_URI[sha256sum] = "9efcc9fab3b49ab833475702b55edd5ae07af1af7a4c627678980b45e459c460"

BBCLASSEXTEND = "native nativesdk"
