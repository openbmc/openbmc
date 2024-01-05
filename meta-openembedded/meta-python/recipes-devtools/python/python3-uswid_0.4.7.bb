SUMMARY = "A pure-python library for embedding CoSWID data"
HOMEPAGE = "https://github.com/hughsie/python-uswid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40d2542b8c43a3ec2b7f5da31a697b88"

SRC_URI[sha256sum] = "de15c2421bedaa5f54606558700c1f628f07d73da49ec69d1888214ac52c49e6"

inherit setuptools3 python3native pypi

DEPENDS += " python3-cbor2 python3-lxml python3-pefile"
RDEPENDS:${PN} += " \
    python3-cbor2 \
    python3-json \
    python3-lxml \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
