SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f02e99f7f3c9a7fe8ecfc5d44c2be62"

SRC_URI[md5sum] = "017e1023484ebf436d3514ebeaf2e7e9"
SRC_URI[sha256sum] = "c6abebec9c8833baaf1c51dd1b0259246d1d50b9b50e9a4aa66f33b1e98b8d17"

RDEPENDS_${PN} += " \
    python-subprocess \
    "

inherit pypi setuptools
