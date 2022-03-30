SUMMARY = "A simple optionally-async python inotify library, focused on simplicity of use and operation, and leveraging modern Python features"
HOMEPAGE = "https://gitlab.com/Taywee/asyncinotify"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec941a1cd6616454970d03cb9c9e8f8"

SRC_URI[sha256sum] = "867cc056d88fc07aa8b3d1dc5b9c3c911cdd6130a4df5f67beb1fdecfd37b164"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-core  \
    python3-ctypes \
    python3-io \
"
