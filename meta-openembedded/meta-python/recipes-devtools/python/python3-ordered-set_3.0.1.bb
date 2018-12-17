SUMMARY = "A MutableSet that remembers its order, so that every entry has an index."
HOMEPAGE = "http://github.com/LuminosoInsight/ordered-set"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-LICENSE;md5=2b36be0d99854aa2ae292a800a7c1d4e"

SRC_URI[md5sum] = "a8059c7b99cde0f8dda01ddee6b43c2c"
SRC_URI[sha256sum] = "3d6fd7bffbb15f613a9e8a6281bf97c2d67f7bb8677deca8249df2fbdd9cce7b"

inherit pypi setuptools3

DEPENDS += "python3-pytest-runner-native"
