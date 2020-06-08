SUMMARY = "httplib2 caching for requests"
HOMEPAGE = "https://pypi.org/project/CacheControl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6dc7e1b428eda03d850209fdbd6c71f1"

# On PyPi, this is "CacheControl", rather than "cachecontrol", so we need to
# override PYPI_PACKAGE so fetch succeeds.
PYPI_PACKAGE = "CacheControl"

SRC_URI[md5sum] = "5890b797f9b48b2b4cd1448cca89e396"
SRC_URI[sha256sum] = "be9aa45477a134aee56c8fac518627e1154df063e85f67d4f83ce0ccc23688e8"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-crypt \
    python3-datetime \
    python3-email \
    python3-lockfile \
    python3-json \
    python3-logging \
    python3-msgpack \
    python3-netclient \
    python3-pickle \
    python3-requests \
    python3-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
