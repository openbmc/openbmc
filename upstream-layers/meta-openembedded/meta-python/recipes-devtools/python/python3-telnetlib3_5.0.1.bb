SUMMARY = "Telnet server and client library based on asyncio"
HOMEPAGE = "https://github.com/jquast/telnetlib3"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=15abe157ad6f0b483975cc34bcc1aa99"

SRC_URI[sha256sum] = "6fde197ffb71cf26ad27fe2c88694d8364f4b1cc962a37762e0cf7e79b9afaed"


inherit pypi python_setuptools_build_meta python_hatchling

RDEPENDS:${PN} = "\
    python3-asyncio \
    python3-wcwidth \
"
