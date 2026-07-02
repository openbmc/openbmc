SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "48a0ef30a42db65d8f3399045e1d09ab0ba66e3b9efc360d07f80ea55d286025"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-compression \
    python3-netclient \
    python3-pyparsing \
"

CVE_PRODUCT = "httplib2"
