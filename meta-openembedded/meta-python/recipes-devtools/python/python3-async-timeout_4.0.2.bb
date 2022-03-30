SUMMARY = "asyncio-compatible timeout context manager"
DESCRIPTION = "\
The context manager is useful in cases when you want to apply \
timeout logic around block of code or in cases when asyncio.wait_for() \
is not suitable. Also it's much faster than asyncio.wait_for() because \
timeout doesn't create a new task."
HOMEPAGE = "https://github.com/aio-libs/async-timeout"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fa41f15bb5f23b6d3560c5845eb8d57"

SRC_URI[sha256sum] = "2163e1640ddb52b7a8c80d0a67a08587e5d245cc9c553a74a847056bc2976b15"

PYPI_PACKAGE = "async-timeout"
inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
