SUMMARY = "Telnet server and client library based on asyncio"
HOMEPAGE = "https://github.com/jquast/telnetlib3"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=15abe157ad6f0b483975cc34bcc1aa99"

SRC_URI[sha256sum] = "37f584609917a3394302cac6c2f78683add80115820c19a4ce35e0fd499f6ad6"

PYPI_PACKAGE = "telnetlib3"

inherit pypi python_setuptools_build_meta python_hatchling

RDEPENDS:${PN} = "\
    python3-asyncio \
    python3-wcwidth \
"
