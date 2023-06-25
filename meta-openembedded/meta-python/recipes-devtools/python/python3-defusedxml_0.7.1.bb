SUMMARY = "XML bomb protection for Python stdlib modules"
DESCRIPTION = "Python package with modified subclasses of all stdlib XML \
parsers that prevent any potentially malicious operation."

LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=056fea6a4b395a24d0d278bf5c80249e"

SRC_URI[sha256sum] = "1bb3032db185915b62d7c6209c5a8792be6a32ab2fedacc84e01b52c51aa3e69"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "python3-xml"
