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
                    file://COPYING-MIT;md5=60872a112595004233b769b6cbfd65b6 \
                    "

SRC_URI[md5sum] = "0b387d4609ed20c88baede8579a4d425"
SRC_URI[sha256sum] = "ec7dd291545842295b7b56c12c90ffad2976cc7070c98d7b1517b7b6cd5994b3"

inherit pypi setuptools3

DEPENDS = "\
    curl \
    ${PYTHON_PN}\
"
