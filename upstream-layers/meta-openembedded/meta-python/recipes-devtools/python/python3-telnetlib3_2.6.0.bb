SUMMARY = "Telnet server and client library based on asyncio"
HOMEPAGE = "https://github.com/jquast/telnetlib3"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=15abe157ad6f0b483975cc34bcc1aa99"

SRC_URI[sha256sum] = "c231e790c626f5b6927a4a85e79bce18cde994c9424254d2193582b430972164"

PYPI_PACKAGE = "telnetlib3"

inherit pypi python_setuptools_build_meta python_hatchling

RDEPENDS:${PN} = "\
    python3-asyncio \
    python3-wcwidth \
"
