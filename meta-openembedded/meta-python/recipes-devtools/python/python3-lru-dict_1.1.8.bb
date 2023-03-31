SUMMARY = "A fixed size dict like container which evicts Least Recently Used (LRU) items once size limit is exceeded."
HOMEPAGE = "https://github.com/amitdev/lru-dict"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d10a486ee04034fdef5162fd791f153"

SRC_URI[sha256sum] = "878bc8ef4073e5cfb953dfc1cf4585db41e8b814c0106abde34d00ee0d0b3115"

inherit pypi setuptools3

SRC_URI += "file://0001-lru-Use-PyCFunction-instead-of-PyCFunctionWithKeywor.patch"
