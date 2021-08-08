SUMMARY = "Simple module to parse ISO 8601 dates"
HOMEPAGE = "http://pyiso8601.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b05625f2336fa024e8d57e65c6595844"

SRC_URI[sha256sum] = "36532f77cc800594e8f16641edae7f1baf7932f05d8e508545b95fc53c6dc85b"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
