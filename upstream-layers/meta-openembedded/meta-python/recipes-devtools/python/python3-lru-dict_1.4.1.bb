DESCRIPTION = "A fixed size dict like container which evicts Least Recently Used (LRU) items once size limit is exceeded."
HOMEPAGE = "https://github.com/amitdev/lru-dict"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d10a486ee04034fdef5162fd791f153"

SRC_URI += "file://0001-pyproject.toml-do-not-pin-setuptools.patch"
SRC_URI[sha256sum] = "cc518ff2d38cc7a8ab56f9a6ae557f91e2e1524b57ed8e598e97f45a2bd708fc"

inherit pypi python_setuptools_build_meta
