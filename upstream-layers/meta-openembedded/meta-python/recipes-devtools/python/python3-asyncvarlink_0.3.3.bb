SUMMARY = "asyncio-compatible, type-driven Varlink implementation"
DESCRIPTION = "An asyncio Varlink binding supporting file-descriptor passing, \
with interfaces derived from annotated Python classes."
HOMEPAGE = "https://github.com/helmutg/asyncvarlink"
SECTION = "devel/python"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9ec28527f3d544b51ceb0e1907d0bf3f"

SRC_URI[sha256sum] = "0270e352d5c8d5348b90f547a4215bfb37a6b829b84341822f80273e830dadd4"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} = "\
    python3-asyncio \
    python3-core \
    python3-io \
    python3-json \
    python3-logging \
"

# tests/test_conversion.py:ConversionTests.test_invalid fails with less than ~2GB of RAM
RDEPENDS:${PN}-ptest += "python3-hypothesis python3-netclient python3-unittest"
