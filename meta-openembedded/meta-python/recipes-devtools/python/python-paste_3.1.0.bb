SUMMARY = "Tools for using a Web Server Gateway Interface stack"
HOMEPAGE = "http://pythonpaste.org/"
LICENSE = "MIT"
RDEPENDS_${PN} = "python-six"

LIC_FILES_CHKSUM = "file://docs/license.txt;md5=1798f29d55080c60365e6283cb49779c"

SRC_URI[md5sum] = "904ec5634f3f901cadf529711930a98b"
SRC_URI[sha256sum] = "18323f22df5ab6998fdf4c5aa5a9f41d33ff949a87ad7b2ca48e72fbf50fa3e4"

PYPI_PACKAGE = "Paste"
inherit pypi setuptools

FILES_${PN} += "/usr/lib/*"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

