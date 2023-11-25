SUMMARY = "Code coverage measurement for Python"
HOMEPAGE = "https://coverage.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "be32ad29341b0170e795ca590e1c07e81fc061cb5b10c74ce7203491484404ef"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-io \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-shell \
    python3-sqlite3 \
    python3-tomllib \
    python3-xml \
"
