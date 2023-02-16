SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI += "file://0001-cleanup-Drop-using-register-storage-class-keyword-ev.patch"
SRC_URI[sha256sum] = "e7c8dc13af7db097bed64a051d2dd49e9f0af495c26995c00a9ee842690d34c0"

inherit pypi setuptools3
