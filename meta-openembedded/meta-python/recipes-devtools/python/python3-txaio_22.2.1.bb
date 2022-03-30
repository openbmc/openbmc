DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[sha256sum] = "2e4582b70f04b2345908254684a984206c0d9b50e3074a24a4c55aba21d24d01"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-twisted \
"

BBCLASSEXTEND = "native nativesdk"
