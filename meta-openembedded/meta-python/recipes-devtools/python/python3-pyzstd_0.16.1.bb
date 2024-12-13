DESCRIPTION = "Pyzstd module provides classes and functions for compressing and \
decompressing data, using Facebook’s Zstandard (or zstd as short name) algorithm."
HOMEPAGE = "https://github.com/animalize/pyzstd"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aedb5a2679cd1552fb61c181ef974b9e"

PYPI_PACKAGE = "pyzstd"

SRC_URI[sha256sum] = "ed50c08233878c155c73ab2622e115cd9e46c0f1c2e2ddd76f2e7ca24933f195"

inherit pypi setuptools3

# clang-16 with -flto segfaults on arm, therefore ignore flto for now
do_configure:append:arm:toolchain-clang() {
    sed -i -e "s|'-flto'|''|" ${S}/setup.py
}
