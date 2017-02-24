SUMMARY = "Python lightweight in-process concurrent programming"
LICENSE = "MIT & PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03143d7a1a9f5d8a0fee825f24ca9c36 \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI += "\
           file://0001-Use-x-instead-of-r-for-aarch64-register-names.patch \
           "
SRC_URI[md5sum] = "c6659cdb2a5e591723e629d2eef22e82"
SRC_URI[sha256sum] = "58b2f3a2e7075c655616bf95e82868db4980f3bb6661db70ad02a51e4ddd2252"

PYPI_PACKAGE_EXT = "zip"
inherit pypi distutils

