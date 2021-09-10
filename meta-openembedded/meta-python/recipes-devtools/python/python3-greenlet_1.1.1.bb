SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "c0f22774cd8294078bdf7392ac73cf00bfa1e5e0ed644bd064fdabc5f2a2f481"

inherit pypi distutils3 setuptools3
