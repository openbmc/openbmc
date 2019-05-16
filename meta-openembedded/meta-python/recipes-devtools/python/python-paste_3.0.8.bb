SUMMARY = "Tools for using a Web Server Gateway Interface stack"
HOMEPAGE = "http://pythonpaste.org/"
LICENSE = "MIT"
RDEPENDS_${PN} = "python-six"

LIC_FILES_CHKSUM = "file://docs/license.txt;md5=1798f29d55080c60365e6283cb49779c"

SRC_URI[md5sum] = "0119f4e82b930781e3bcb59c77f7a353"
SRC_URI[sha256sum] = "c4f13993e6ab4a38602cef298d876552083e8215685bb45abda7113f0dd48117"

PYPI_PACKAGE = "Paste"
inherit pypi setuptools

FILES_${PN} += "/usr/lib/*"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

