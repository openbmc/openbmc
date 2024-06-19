DESCRIPTION = "Google API client core library"
HOMEPAGE = "https://github.com/googleapis/python-api-core"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3

SRC_URI[sha256sum] = "cf1b7c2694047886d2af1128a03ae99e391108a08804f87cfd35970e49c9cd10"

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-datetime \
    python3-logging \
    python3-math \
"

RDEPENDS:${PN} += "\
    python3-googleapis-common-protos \
    python3-google-auth \
    python3-grpcio \
    python3-protobuf \
    python3-pytz \
    python3-requests \
    python3-six \
"
