SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "2eaf067fc6d886931c7962e8c6bede15d2f01965560f3359b27c80bde2d151f2"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
