SUMMARY = "XML bomb protection for Python stdlib modules"
DESCRIPTION = "Python package with modified subclasses of all stdlib XML \
parsers that prevent any potentially malicious operation."

LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=056fea6a4b395a24d0d278bf5c80249e"

SRC_URI[md5sum] = "a59741f675c4cba649de40a99f732897"
SRC_URI[sha256sum] = "f684034d135af4c6cbb949b8a4d2ed61634515257a67299e5f940fbaa34377f5"

inherit pypi setuptools3
