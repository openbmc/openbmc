DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[sha256sum] = "17938f2bca4a9cabce61346758e482ca4e600160cbc28e861493eac74a19539d"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-twisted \
"

BBCLASSEXTEND = "native nativesdk"
