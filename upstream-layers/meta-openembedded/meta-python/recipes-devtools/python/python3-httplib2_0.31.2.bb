SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "385e0869d7397484f4eab426197a4c020b606edd43372492337c0b4010ae5d24"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-compression \
    python3-netclient \
    python3-pyparsing \
"

CVE_PRODUCT = "httplib2"
