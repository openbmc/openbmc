SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "a61efc018fd3eb317eeca31aba90ee9e7f26f22884a79b6c6ec715bf71bb62f1"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
