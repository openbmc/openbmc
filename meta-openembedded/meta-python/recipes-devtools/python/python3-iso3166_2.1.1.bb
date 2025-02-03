SUMMARY = "Self-contained ISO 3166-1 country definitions"
HOMEPAGE = "https://pypi.org/project/iso3166/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5e2f4edc7e7408a82e4a1d05f229b695"

SRC_URI[sha256sum] = "fcd551b8dda66b44e9f9e6d6bbbee3a1145a22447c0a556e5d0fb1ad1e491719"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += "python3-numbers"

BBCLASSEXTEND = "native nativesdk"
