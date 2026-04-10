SUMMARY = "Linux Test Project tests executor"
HOMEPAGE = "https://github.com/linux-test-project/kirk"
DESCRIPTION = "Kirk application is a fork of runltp-ng and it’s the official \
LTP tests executor. It provides support for remote testing via Qemu, SSH, \
LTX, parallel execution and much more."
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

SRC_URI[sha256sum] = "4606f46dfdb7be1e1c2de8d8f040037459a9de8a07fa1123cee4b68c7f7cb764"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "kirk"

RDEPENDS:${PN} += "python3-asyncio python3-json"
