DESCRIPTION = "Pyzstd module provides classes and functions for compressing and \
decompressing data, using Facebook’s Zstandard (or zstd as short name) algorithm."
HOMEPAGE = "https://github.com/animalize/pyzstd"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8458383225d7107f3383ee5c521628d2"

PYPI_PACKAGE = "pyzstd"

SRC_URI[sha256sum] = "cbfdde6c5768ffa5d2f14127bbc1d7c3c2d03c0ceaeb0736946197e06275ccc7"

inherit pypi setuptools3

# clang-16 with -flto segfaults on arm, therefore ignore flto for now
do_configure:append:arm:toolchain-clang() {
    sed -i -e "s|'-flto'|''|" ${S}/setup.py
}
