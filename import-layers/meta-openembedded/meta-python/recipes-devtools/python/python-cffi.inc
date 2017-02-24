SUMMARY = "Foreign Function Interface for Python calling C code"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS = "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "c8e877fe0426a99d0cf5872cf2f95b27"
SRC_URI[sha256sum] = "c321bd46faa7847261b89c0469569530cad5a41976bb6dba8202c0159f476568"

BBCLASSEXTEND = "native nativesdk"
