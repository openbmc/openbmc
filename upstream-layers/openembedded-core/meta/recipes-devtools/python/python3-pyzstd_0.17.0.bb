DESCRIPTION = "Pyzstd module provides classes and functions for compressing and \
decompressing data, using Facebook’s Zstandard (or zstd as short name) algorithm."
SUMMARY = "Python bindings to Zstandard (zstd) compression library"
HOMEPAGE = "https://github.com/animalize/pyzstd"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aedb5a2679cd1552fb61c181ef974b9e"

PYPI_PACKAGE = "pyzstd"

SRC_URI += "file://0001-Remove-setuptools-version-limit-of-74.patch"
SRC_URI[sha256sum] = "d84271f8baa66c419204c1dd115a4dec8b266f8a2921da21b81764fa208c1db6"

inherit pypi python_setuptools_build_meta ptest-python-pytest

# clang-16 with -flto segfaults on arm, therefore ignore flto for now
do_configure:append:arm:toolchain-clang() {
    sed -i -e "s|'-flto'|''|" ${S}/setup.py
}
