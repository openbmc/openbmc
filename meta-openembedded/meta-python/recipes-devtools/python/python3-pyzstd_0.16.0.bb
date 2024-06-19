DESCRIPTION = "Pyzstd module provides classes and functions for compressing and \
decompressing data, using Facebook’s Zstandard (or zstd as short name) algorithm."
HOMEPAGE = "https://github.com/animalize/pyzstd"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aedb5a2679cd1552fb61c181ef974b9e"

PYPI_PACKAGE = "pyzstd"

SRC_URI[sha256sum] = "fd43a0ae38ae15223fb1057729001829c3336e90f4acf04cf12ebdec33346658"

inherit pypi setuptools3

# clang-16 with -flto segfaults on arm, therefore ignore flto for now
do_configure:append:arm:toolchain-clang() {
    sed -i -e "s|'-flto'|''|" ${S}/setup.py
}
