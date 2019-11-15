SUMMARY = "A zero-dependency DBus library for Python with asyncio support"
HOMEPAGE = "https://github.com/acrisci/python-dbus-next"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=b32e18a71bcdd072bce21f204629a104"

SRC_URI[md5sum] = "df838d695284dd1775860f9691a8663f"
SRC_URI[sha256sum] = "a567d845ceed5feac48dda7faeb9ff2571f9a434a3c32b9b363f763e82368762"

PYPI_PACKAGE = "dbus_next"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
