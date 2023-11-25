SUMMARY = "This module provides ctypes-based bindings for the native libvlc API of the VLC video player."
HOMEPAGE = "wiki.videolan.org/PythonBinding"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "244fbb9e392a0326841fca926d6d12a2a36c546982191f493f148fa19e66b1d4"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
