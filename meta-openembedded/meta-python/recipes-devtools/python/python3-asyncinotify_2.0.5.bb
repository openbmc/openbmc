SUMMARY = "A simple optionally-async python inotify library, focused on simplicity of use and operation, and leveraging modern Python features"
HOMEPAGE = "https://gitlab.com/Taywee/asyncinotify"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec941a1cd6616454970d03cb9c9e8f8"

SRC_URI[sha256sum] = "e4d95cba362f10d33b6fdd558afd39f0ea7a5be1a9694434e738141cbed366d9"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-core  \
    python3-ctypes \
    python3-io \
"
