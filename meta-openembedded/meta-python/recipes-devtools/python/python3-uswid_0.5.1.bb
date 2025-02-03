SUMMARY = "A pure-python library for embedding CoSWID data"
HOMEPAGE = "https://github.com/hughsie/python-uswid"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause-Patent"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3636dfe71e94cc72918423cec0d1971"

SRC_URI[sha256sum] = "92b48a8f6601687c1cc556878571abf9ddb0bdbaf244713b7ff838c48fcb68b3"

inherit setuptools3 python3native pypi

DEPENDS += " python3-cbor2 python3-lxml python3-pefile"
RDEPENDS:${PN} += " \
    python3-cbor2 \
    python3-json \
    python3-lxml \
    python3-netclient \
    python3-pefile \
"

BBCLASSEXTEND = "native nativesdk"
