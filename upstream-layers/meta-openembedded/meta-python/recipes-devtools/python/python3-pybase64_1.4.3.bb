SUMMARY = "Fast Base64 encoding/decoding in Python"
HOMEPAGE = "https://github.com/mayeut/pybase64"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84b11fa55a5d83cf6fa202fd3b49c7e8"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "c2ed274c9e0ba9c8f9c4083cfe265e66dd679126cd9c2027965d807352f3f053"

BBCLASSEXTEND = "native nativesdk"
