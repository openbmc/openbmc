SUMMARY = "Simple module to parse ISO 8601 dates"
HOMEPAGE = "http://pyiso8601.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b05625f2336fa024e8d57e65c6595844"

SRC_URI[sha256sum] = "27f503220e6845d9db954fb212b95b0362d8b7e6c1b2326a87061c3de93594b1"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
