SUMMARY = "Python tools to analyze security characteristics of MS Office and OLE files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=062477247e75fcb78ae3e1280be9e4e1"

SRC_URI[sha256sum] = "ad452099f4695ffd8855113f453348200d195ee9fa341a09e197d66ee7e0b2c3"

inherit pypi setuptools3

PYPI_PACKAGE = "oletools"
PYPI_PACKAGE_EXT = "zip"
