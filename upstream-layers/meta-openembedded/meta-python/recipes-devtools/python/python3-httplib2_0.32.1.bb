SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "37900eb8a74576bd99fc39ae1dc8409f95f965d45b56cbcb2194bb55f58a90e1"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += "\
    python3-compression \
    python3-netclient \
    python3-pyparsing \
"

CVE_PRODUCT = "httplib2_project:httplib2"
