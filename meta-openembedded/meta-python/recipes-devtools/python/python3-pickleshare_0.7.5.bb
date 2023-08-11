SUMMARY = "Tiny 'shelve'-like database with concurrency support"
HOMEPAGE = "https://github.com/pickleshare/pickleshare"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=905c08218089ffebea3a64c82fc4d7d0"

PYPI_PACKAGE = "pickleshare"

SRC_URI[md5sum] = "44ab782615894a812ab96669a122a634"
SRC_URI[sha256sum] = "87683d47965c1da65cdacaf31c8441d12b8044cdec9aca500cd78fc2c683afca"

inherit setuptools3 pypi

RDEPENDS:${PN} += "python3-pickle"
