SUMMARY = "A Python Interface To The cURL library"
DESCRIPTION = "\
PycURL is a Python interface to libcurl, the multiprotocol file \
transfer library. Similarly to the urllib Python module, PycURL can \
be used to fetch objects identified by a URL from a Python program \
"
SECTION = "devel/python"
HOMEPAGE = "http://pycurl.io/"

LICENSE = "LGPLv2 | MIT"
LIC_FILES_CHKSUM = "file://COPYING-LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING-MIT;md5=75f131c591546fd1277ca49c9a81ab1b \
                    "

SRC_URI[sha256sum] = "2ce9905626d8ceafcbadee666e2f45397e29c7618ddcdc63fc22d85e5046c6d6"

inherit pypi setuptools3

DEPENDS = "\
    curl \
    ${PYTHON_PN}\
"
