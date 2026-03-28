SUMMARY = "Python module to interface with the pkg-config command line too"
HOMEPAGE = "https://github.com/matze/pkgconfig"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faa7f82be8f220bff6156be4790344fc"

SRC_URI[sha256sum] = "4a5a6631ce937fafac457104a40d558785a658bbdca5c49b6295bc3fd651907f"

RDEPENDS:${PN} = "pkgconfig \
                 python3-shell \
                 "

inherit pypi python_poetry_core

BBCLASSEXTEND = "native"

