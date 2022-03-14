DESCRIPTION = "Backports and enhancements for the contextlib module"
HOMEPAGE = "http://contextlib2.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "PSF & Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d2b6be100f1002194122ec9bfee7b8b4"

SRC_URI[sha256sum] = "ab1e2bfe1d01d968e1b7e8d9023bc51ef3509bba217bb730cee3827e1ee82869"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
