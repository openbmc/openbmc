SUMMARY = "This module provides ctypes-based bindings for the native libvlc API of the VLC video player."
HOMEPAGE = "wiki.videolan.org/PythonBinding"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "1039bde287853b4b7b61ba22d83761832434f78506da762dfb060291bf32897d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
