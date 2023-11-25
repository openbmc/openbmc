SUMMARY = "Colorful worry-free console applications for Linux, Mac OS X, and Windows."
HOMEPAGE = "https://github.com/matthewdeanmartin/colorclass"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1b2a533055839e54558a727657c1c73e"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6d4fe287766166a98ca7bc6f6312daf04a0481b1eda43e7173484051c0ab4366"

PYPI_PACKAGE="colorclass"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
