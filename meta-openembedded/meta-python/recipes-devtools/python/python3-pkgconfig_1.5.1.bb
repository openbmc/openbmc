SUMMARY = "Python module to interface with the pkg-config command line too"
HOMEPAGE = "http://github.com/matze/pkgconfig"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faa7f82be8f220bff6156be4790344fc"

SRC_URI[md5sum] = "9f9cdb224ec0a1e59efcc7cac4b91972"
SRC_URI[sha256sum] = "97bfe3d981bab675d5ea3ef259045d7919c93897db7d3b59d4e8593cba8d354f"

RDEPENDS_${PN} = "pkgconfig \
                 ${PYTHON_PN}-shell \
                 "

inherit pypi setuptools3

BBCLASSEXTEND = "native"

