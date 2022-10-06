SUMMARY = "httplib2 caching for requests"
HOMEPAGE = "https://pypi.org/project/CacheControl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=12;endline=12;md5=942a721dac34d073453642ffe5fcf546"

# On PyPi, this is "CacheControl", rather than "cachecontrol", so we need to
# override PYPI_PACKAGE so fetch succeeds.
PYPI_PACKAGE = "CacheControl"

SRC_URI[sha256sum] = "9c2e5208ea76ebd9921176569743ddf6d7f3bb4188dbf61806f0f8fc48ecad38"

inherit pypi setuptools3

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
