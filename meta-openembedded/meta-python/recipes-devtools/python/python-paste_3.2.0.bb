SUMMARY = "Tools for using a Web Server Gateway Interface stack"
HOMEPAGE = "http://pythonpaste.org/"
LICENSE = "MIT"
RDEPENDS_${PN} = "python-six"

LIC_FILES_CHKSUM = "file://docs/license.txt;md5=1798f29d55080c60365e6283cb49779c"

SRC_URI[md5sum] = "9225991c1c37a81e0aaac2fb046f3602"
SRC_URI[sha256sum] = "2153da2f1b09a69bce7633d7e3f9aaa802572e85f9ac1ed09ad93ef8599d31b6"

PYPI_PACKAGE = "Paste"
inherit pypi setuptools

FILES_${PN} += "/usr/lib/*"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

