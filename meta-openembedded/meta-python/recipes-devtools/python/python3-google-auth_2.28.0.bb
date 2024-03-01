DESCRIPTION = "Google Authentication Library"
HOMEPAGE = "https://github.com/googleapis/google-auth-library-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit pypi setuptools3

SRC_URI[sha256sum] = "3cfc1b6e4e64797584fb53fc9bd0b7afa9b7c0dba2004fa7dcc9349e58cc3195"

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-datetime \
    python3-io \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-numbers \
"

RDEPENDS:${PN} += "\
    python3-aiohttp \
    python3-cachetools \
    python3-pyasn1-modules \
    python3-rsa \
    python3-six \
"
