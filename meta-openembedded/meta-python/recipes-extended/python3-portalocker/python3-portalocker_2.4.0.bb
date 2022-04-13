SUMMARY = "Cross-platform locking library"
DESCRIPTION = "Portalocker is a library to provide an easy API to file locking"
LICENSE = "PSF-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f9273424c73af966635d66eb53487e14"

SRC_URI[sha256sum] = "a648ad761b8ea27370cb5915350122cd807b820d2193ed5c9cc28f163df637f4"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
