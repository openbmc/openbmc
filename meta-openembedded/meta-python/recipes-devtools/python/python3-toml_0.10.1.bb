SUMMARY = "Python Library for Tom's Obvious, Minimal Language"
HOMEPAGE = "https://github.com/uiri/toml"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16c77b2b1050d2f03cb9c2ed0edaf4f0"

SRC_URI[md5sum] = "743131c431419fe42f854cff02ad3abe"
SRC_URI[sha256sum] = "926b612be1e5ce0634a2ca03470f95169cf16f939018233a670519cb4ac58b0f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
