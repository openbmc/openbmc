SUMMARY = "Ultimate transformation library that supports validation, contexts and aiohttp."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=21ecc7aa8f699874e706fc1354903437"

SRC_URI[sha256sum] = "d9d00800318fbd343fdfb3353e947b2ebb5557159c844696c5ac24846f76d41c"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-numbers \
    python3-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
