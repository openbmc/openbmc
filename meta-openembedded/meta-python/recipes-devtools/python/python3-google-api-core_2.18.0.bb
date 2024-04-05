DESCRIPTION = "Google API client core library"
HOMEPAGE = "https://github.com/googleapis/python-api-core"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3

SRC_URI[sha256sum] = "62d97417bfc674d6cef251e5c4d639a9655e00c45528c4364fbfebb478ce72a9"

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
