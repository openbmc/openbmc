SUMMARY = "Code coverage measurement for Python"
HOMEPAGE = "https://coverage.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "1ed4b95480952b1a26d863e546fa5094564aa0065e1e5f0d4d0041f293251d04"

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

BBCLASSEXTEND = "native nativesdk"
