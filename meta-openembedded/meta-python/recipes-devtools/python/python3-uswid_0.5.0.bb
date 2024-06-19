SUMMARY = "A pure-python library for embedding CoSWID data"
HOMEPAGE = "https://github.com/hughsie/python-uswid"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause-Patent"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3636dfe71e94cc72918423cec0d1971"

SRC_URI[sha256sum] = "bdcd7ee5afac1da60ee688c357aa12f5f8d74bc28012446b10e2b4a9cf52fc6d"

inherit setuptools3 python3native pypi

DEPENDS += " python3-cbor2 python3-lxml python3-pefile"
RDEPENDS:${PN} += " \
    python3-cbor2 \
    python3-json \
    python3-lxml \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
