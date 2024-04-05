SUMMARY = "Python PE parsing module"
DESCRIPTION = "A multi-platform Python module to parse and work with Portable Executable (PE) files."
HOMEPAGE = "https://github.com/erocarrera/pefile"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38066667888b01d8118ff9cc23da1873"

inherit setuptools3 pypi ptest
SRC_URI[sha256sum] = "82e6114004b3d6911c77c3953e3838654b04511b8b66e8583db70c65998017dc"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-mmap \
    python3-netclient \
    python3-stringold \
"
