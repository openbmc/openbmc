SUMMARY = "A simple optionally-async python inotify library, focused on simplicity of use and operation, and leveraging modern Python features"
HOMEPAGE = "https://gitlab.com/Taywee/asyncinotify"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f75d2927d3c1ed2414ef72048f5ad640"

SRC_URI[sha256sum] = "438bdc0715f4f959e6e0af70e76cea198b4e28e5933c7cbf1e9987b50394afb3"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-core  \
    python3-ctypes \
    python3-io \
"
