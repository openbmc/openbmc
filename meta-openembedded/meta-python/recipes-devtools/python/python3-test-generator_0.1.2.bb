SUMMARY = "Generator is a helper for generating test methods for nose while still using unittest."
DESCRIPTION = "Python package with modified subclasses of all stdlib XML \
parsers that prevent any potentially malicious operation."

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=041a2bff595d40ccb4b36356f89dab00"

SRC_URI[md5sum] = "6c69e73ba5b4b3ed62f7bcda071c64f1"
SRC_URI[sha256sum] = "ad5925c814bfe79497b43df096e3bb52c166d1577f7aff160137301676232f4a"

inherit pypi setuptools3

DEPENDS += "python3-nose-native"

RDEPENDS_${PN} += "python3-six"
