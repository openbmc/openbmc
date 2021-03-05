SUMMARY = "Simple module to parse ISO 8601 dates"
HOMEPAGE = "http://pyiso8601.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b05625f2336fa024e8d57e65c6595844"

SRC_URI[sha256sum] = "8aafd56fa0290496c5edbb13c311f78fa3a241f0853540da09d9363eae3ebd79"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
