SUMMARY = "Python module to interface with the pkg-config command line too"
HOMEPAGE = "http://github.com/matze/pkgconfig"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faa7f82be8f220bff6156be4790344fc"

SRC_URI[sha256sum] = "deb4163ef11f75b520d822d9505c1f462761b4309b1bb713d08689759ea8b899"

RDEPENDS:${PN} = "pkgconfig \
                 python3-shell \
                 "

inherit pypi python_poetry_core

BBCLASSEXTEND = "native"

