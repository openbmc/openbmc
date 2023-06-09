SUMMARY = "A fixed size dict like container which evicts Least Recently Used (LRU) items once size limit is exceeded."
HOMEPAGE = "https://github.com/amitdev/lru-dict"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d10a486ee04034fdef5162fd791f153"

SRC_URI[sha256sum] = "13c56782f19d68ddf4d8db0170041192859616514c706b126d0df2ec72a11bd7"

inherit pypi setuptools3

SRC_URI += "${PYPI_SRC_URI}"
