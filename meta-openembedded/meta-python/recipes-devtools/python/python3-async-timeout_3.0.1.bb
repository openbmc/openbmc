SUMMARY = "asyncio-compatible timeout context manager"
DESCRIPTION = "\
The context manager is useful in cases when you want to apply \
timeout logic around block of code or in cases when asyncio.wait_for() \
is not suitable. Also it's much faster than asyncio.wait_for() because \
timeout doesn't create a new task."
HOMEPAGE = "https://github.com/aio-libs/async-timeout"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI[md5sum] = "305c4fa529f2485c403d0dbe14390175"
SRC_URI[sha256sum] = "0c3c816a028d47f659d6ff5c745cb2acf1f966da1fe5c19c77a70282b25f4c5f"

PYPI_PACKAGE = "async-timeout"
inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
