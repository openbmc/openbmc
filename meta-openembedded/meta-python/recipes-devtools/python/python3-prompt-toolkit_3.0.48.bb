SUMMARY = "Library for building powerful interactive command lines in Python"
HOMEPAGE = "https://python-prompt-toolkit.readthedocs.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[sha256sum] = "d6623ab0477a80df74e646bdbc93621143f5caf104206aa29294d53de1a03d90"

inherit pypi setuptools3

PYPI_PACKAGE = "prompt_toolkit"
UPSTREAM_CHECK_PYPI_PACKAGE = "prompt_toolkit"

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
