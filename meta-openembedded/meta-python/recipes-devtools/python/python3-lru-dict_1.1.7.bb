SUMMARY = "A fixed size dict like container which evicts Least Recently Used (LRU) items once size limit is exceeded."
HOMEPAGE = "https://github.com/amitdev/lru-dict"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d10a486ee04034fdef5162fd791f153"

SRC_URI[sha256sum] = "45b81f67d75341d4433abade799a47e9c42a9e22a118531dcb5e549864032d7c"

inherit pypi setuptools3
