SUMMARY = "A simple optionally-async python inotify library, focused on simplicity of use and operation, and leveraging modern Python features"
HOMEPAGE = "https://gitlab.com/Taywee/asyncinotify"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec941a1cd6616454970d03cb9c9e8f8"

SRC_URI[sha256sum] = "da812bf2be6a76aac6670b766eec4564d9484da6595d3ce1f9bba8ba78c30445"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-core  \
    python3-ctypes \
    python3-io \
"
