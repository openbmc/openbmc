SUMMARY = "Cross-platform locking library"
DESCRIPTION = "Portalocker is a library to provide an easy API to file locking"
LICENSE = "PSF"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f9273424c73af966635d66eb53487e14"

SRC_URI[md5sum] = "bd4908d035464aa440dd7f262ef78345"
SRC_URI[sha256sum] = "75cfe02f702737f1726d83e04eedfa0bda2cc5b974b1ceafb8d6b42377efbd5f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
