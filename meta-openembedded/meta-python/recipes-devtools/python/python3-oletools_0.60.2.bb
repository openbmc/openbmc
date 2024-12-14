SUMMARY = "Python tools to analyze security characteristics of MS Office and OLE files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=181754d317256f1b037529d3e994e1b4"

SRC_URI[sha256sum] = "ad452099f4695ffd8855113f453348200d195ee9fa341a09e197d66ee7e0b2c3"

inherit pypi setuptools3

PYPI_PACKAGE = "oletools"
PYPI_PACKAGE_EXT = "zip"
