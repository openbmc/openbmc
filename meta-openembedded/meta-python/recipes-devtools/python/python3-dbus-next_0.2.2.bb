SUMMARY = "A zero-dependency DBus library for Python with asyncio support"
HOMEPAGE = "https://github.com/acrisci/python-dbus-next"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94e750c96e56788499b56c81de91431c"

SRC_URI[sha256sum] = "f656a3d3450b670f228248ffb1c3a703a69c4a8cb10cce63b108f17c8bd6c3de"

PYPI_PACKAGE = "dbus_next"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
