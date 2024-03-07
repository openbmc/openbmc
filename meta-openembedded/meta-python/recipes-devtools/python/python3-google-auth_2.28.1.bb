DESCRIPTION = "Google Authentication Library"
HOMEPAGE = "https://github.com/googleapis/google-auth-library-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit pypi setuptools3

SRC_URI[sha256sum] = "34fc3046c257cedcf1622fc4b31fc2be7923d9b4d44973d481125ecc50d83885"

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
