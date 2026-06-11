SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7c28f460fafe7be454fcdcac0b60263"

SRC_URI[sha256sum] = "62dc242fd4ea2a50339f4f5aaaf6ecc55605ee74770d7eb2031e760d90a0d114"

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
