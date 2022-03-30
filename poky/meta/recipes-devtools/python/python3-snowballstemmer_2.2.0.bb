SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://github.com/snowballstem/snowball"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=19139aaf3c8c8fa1ca6edd59c072fb9f"

SRC_URI[sha256sum] = "09b16deb8547d3412ad7b590689584cd0fe25ec8db3be37788be3810cbf19cb1"

PYPI_PACKAGE = "snowballstemmer"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
