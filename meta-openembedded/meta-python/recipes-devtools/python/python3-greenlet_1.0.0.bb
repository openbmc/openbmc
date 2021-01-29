SUMMARY = "Python lightweight in-process concurrent programming"
LICENSE = "MIT & PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "719e169c79255816cdcf6dccd9ed2d089a72a9f6c42273aae12d55e8d35bdcf8"

inherit pypi distutils3 setuptools3
