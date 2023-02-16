SUMMARY = "CUPS bindings for Python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3961efb54421653518521529853444c4"

RDEPENDS:${PN} = "python3"

inherit setuptools3 pypi

PV = "2.0.1"

SRC_URI[sha256sum] = "e880d7d7147959ead5cb34764f08b97b41385b36eb8256e8af1ce163dbcccce8"
