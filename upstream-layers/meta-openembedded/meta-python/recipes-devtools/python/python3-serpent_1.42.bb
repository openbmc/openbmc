SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7c28f460fafe7be454fcdcac0b60263"

SRC_URI[sha256sum] = "8ea082b01f8ba07ecd74e34a9118ac4521bc4594938d912b808c89f1da425506"

inherit pypi ptest-python-pytest setuptools3

# python3-misc for timeit.py
RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-misc \
    python3-pytz \
"

RDEPENDS:${PN} += "\
    python3-netclient \
    python3-numbers \
"
