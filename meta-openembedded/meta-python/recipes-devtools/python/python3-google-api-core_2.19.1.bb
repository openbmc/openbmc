DESCRIPTION = "Google API client core library"
HOMEPAGE = "https://github.com/googleapis/python-api-core"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3

SRC_URI[sha256sum] = "f4695f1e3650b316a795108a76a1c416e6afb036199d1c1f1f110916df479ffd"

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
