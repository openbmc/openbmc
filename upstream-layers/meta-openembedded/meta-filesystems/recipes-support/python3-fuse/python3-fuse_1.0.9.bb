SUMMARY = "Python bindings for libfuse2."

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

inherit setuptools3 pypi pkgconfig

SRC_URI[sha256sum] = "9ed59577c36ab218d700aa2839f021f12c252b357186057572b98e19bc2a8589"

DEPENDS = "fuse python3-setuptools-native"

PYPI_PACKAGE = "fuse_python"
