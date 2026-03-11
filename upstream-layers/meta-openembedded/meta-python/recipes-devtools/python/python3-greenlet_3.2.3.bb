SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "8b0dd8ae4c0d6f5e54ee55ba935eeb3d735a9b58a8a1e5b5cbab64e01a39f365"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
