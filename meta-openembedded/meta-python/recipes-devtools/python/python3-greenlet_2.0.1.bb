SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI += "file://0001-greenlet-Drop-using-register-storage-class-keyword.patch"

SRC_URI[sha256sum] = "42e602564460da0e8ee67cb6d7236363ee5e131aa15943b6670e44e5c2ed0f67"

inherit pypi setuptools3
