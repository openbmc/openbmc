SUMMARY = "This module provides ctypes-based bindings for the native libvlc API of the VLC video player."
HOMEPAGE = "wiki.videolan.org/PythonBinding"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "52d0544b276b11e58b6c0b748c3e0518f94f74b1b4cd328c83a59eacabead1ec"

PYPI_PACKAGE = "python_vlc"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
