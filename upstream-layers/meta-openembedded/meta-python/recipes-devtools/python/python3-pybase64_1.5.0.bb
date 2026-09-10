SUMMARY = "Fast Base64 encoding/decoding in Python"
HOMEPAGE = "https://github.com/mayeut/pybase64"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5042ea9bdb8e0560ebab12bceefcd3f7"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "545ab2a433769e3b8e1ce2b4f7b07218bbde202f4954fbfe52948b2522120727"

BBCLASSEXTEND = "native nativesdk"
