SUMMARY = "Python extension for MurmurHash (MurmurHash3), a set of fast and \
           robust hash functions"
HOMEPAGE = "https://github.com/hajimes/mmh3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2edf2352bb2cd3d7787f05247781b314"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1efc8fec8478e9243a78bb993422cf79f8ff85cb4cf6b79647480a31e0d950a8"

BBCLASSEXTEND = "native nativesdk"
