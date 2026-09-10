SUMMARY = "Library for building powerful interactive command lines in Python"
HOMEPAGE = "https://python-prompt-toolkit.readthedocs.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[sha256sum] = "9ec8a0ad96d5c56148b3f914aa79c1564c3fde5d2e6b876e7bc327e353cf8fa6"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "prompt_toolkit"

RDEPENDS:${PN} += " \
    python3-core \
    python3-six \
    python3-terminal \
    python3-threading \
    python3-wcwidth \
    python3-datetime \
    python3-shell \
    python3-image \
    python3-asyncio \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
