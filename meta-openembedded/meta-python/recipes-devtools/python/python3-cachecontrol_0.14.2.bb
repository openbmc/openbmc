SUMMARY = "httplib2 caching for requests"
HOMEPAGE = "https://pypi.org/project/CacheControl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6572692148079ebbbd800be4b9f36c6d"

SRC_URI[sha256sum] = "7d47d19f866409b98ff6025b6a0fca8e4c791fb31abbd95f622093894ce903a2"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "\
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
    python3-mmap \
"

BBCLASSEXTEND = "native nativesdk"
