SUMMARY = "Fast Base64 encoding/decoding in Python"
HOMEPAGE = "https://github.com/mayeut/pybase64"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5042ea9bdb8e0560ebab12bceefcd3f7"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "aa924f7c2e90349d472d7d57c3680de8d222a32c2d3d07f922ab2f60516e478d"

BBCLASSEXTEND = "native nativesdk"
