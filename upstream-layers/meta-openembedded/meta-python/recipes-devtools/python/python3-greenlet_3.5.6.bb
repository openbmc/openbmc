SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT AND PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "8e67c43bdfc88d5fee6db0d3e40175b362fc95fb85f0412d233b9b203c53a575"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
