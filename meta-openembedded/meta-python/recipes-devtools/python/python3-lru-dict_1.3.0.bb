SUMMARY = "A fixed size dict like container which evicts Least Recently Used (LRU) items once size limit is exceeded."
HOMEPAGE = "https://github.com/amitdev/lru-dict"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d10a486ee04034fdef5162fd791f153"

SRC_URI[sha256sum] = "54fd1966d6bd1fcde781596cb86068214edeebff1db13a2cea11079e3fd07b6b"

inherit pypi setuptools3

SRC_URI += "${PYPI_SRC_URI}"
