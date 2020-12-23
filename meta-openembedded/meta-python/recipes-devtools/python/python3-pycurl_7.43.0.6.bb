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

SRC_URI[md5sum] = "3e121d895101022c30619e1bbf97eb97"
SRC_URI[sha256sum] = "8301518689daefa53726b59ded6b48f33751c383cf987b0ccfbbc4ed40281325"

inherit pypi setuptools3

DEPENDS = "\
    curl \
    ${PYTHON_PN}\
"
