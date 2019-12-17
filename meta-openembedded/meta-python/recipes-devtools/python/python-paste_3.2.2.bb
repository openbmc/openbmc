SUMMARY = "Tools for using a Web Server Gateway Interface stack"
HOMEPAGE = "http://pythonpaste.org/"
LICENSE = "MIT"
RDEPENDS_${PN} = "python-six"

LIC_FILES_CHKSUM = "file://docs/license.txt;md5=1798f29d55080c60365e6283cb49779c"

SRC_URI[md5sum] = "788fceb192bc338f1a3e2a8f78fd42b6"
SRC_URI[sha256sum] = "0b1f4d86f8366f0d4093e5449813792c98e760edc6b7c918f0f29f9ef22ae996"

PYPI_PACKAGE = "Paste"
inherit pypi setuptools

FILES_${PN} += "/usr/lib/*"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

