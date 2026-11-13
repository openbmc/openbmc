SUMMARY = "Linux Test Project tests executor"
HOMEPAGE = "https://github.com/linux-test-project/kirk"
DESCRIPTION = "Kirk application is a fork of runltp-ng and it’s the official \
LTP tests executor. It provides support for remote testing via Qemu, SSH, \
LTX, parallel execution and much more."
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

SRC_URI[sha256sum] = "92007f840a2a0af387b6f0d88f51f5c710853238ff3bd9d2b91a34f731739c06"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-asyncio python3-json"
