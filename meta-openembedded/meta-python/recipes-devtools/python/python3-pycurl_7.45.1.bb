SUMMARY = "A Python Interface To The cURL library"
DESCRIPTION = "\
PycURL is a Python interface to libcurl, the multiprotocol file \
transfer library. Similarly to the urllib Python module, PycURL can \
be used to fetch objects identified by a URL from a Python program \
"
SECTION = "devel/python"
HOMEPAGE = "http://pycurl.io/"

LICENSE = "LGPL-2.0-only | MIT"
LIC_FILES_CHKSUM = "file://COPYING-LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING-MIT;md5=be42e1b1e58c8d59c2901fd747bfc55d \
                    "

SRC_URI[sha256sum] = "a863ad18ff478f5545924057887cdae422e1b2746e41674615f687498ea5b88a"

DEPENDS = "curl"

inherit pypi setuptools3
