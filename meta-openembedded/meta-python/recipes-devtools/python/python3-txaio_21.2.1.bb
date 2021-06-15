DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[sha256sum] = "7d6f89745680233f1c4db9ddb748df5e88d2a7a37962be174c0fd04c8dba1dc8"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-twisted \
"

BBCLASSEXTEND = "native nativesdk"
