SUMMARY = "A Python Interface To The cURL library"
DESCRIPTION = "\
PycURL is a Python interface to libcurl, the multiprotocol file \
transfer library. Similarly to the urllib Python module, PycURL can \
be used to fetch objects identified by a URL from a Python program \
"
SECTION = "devel/python"
HOMEPAGE = "http://pycurl.io/"

LICENSE = "LGPL-2.1-only | MIT"
LIC_FILES_CHKSUM = "file://COPYING-LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING-MIT;md5=be42e1b1e58c8d59c2901fd747bfc55d \
                    "

SRC_URI[sha256sum] = "9d43013002eab2fd6d0dcc671cd1e9149e2fc1c56d5e796fad94d076d6cb69ef"

DEPENDS = "curl"

inherit pypi setuptools3

BBCLASSEXTEND  += "native nativesdk"
