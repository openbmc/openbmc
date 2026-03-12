SUMMARY = "Code coverage measurement for Python"
HOMEPAGE = "https://coverage.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "e5c8f6ed1e61a8b2dcdf31eb0b9bbf0130750ca79c1c49eb898e2ad86f5ccc91"

inherit pypi python_setuptools_build_meta

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
