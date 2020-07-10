SUMMARY = "A zero-dependency DBus library for Python with asyncio support"
HOMEPAGE = "https://github.com/acrisci/python-dbus-next"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94e750c96e56788499b56c81de91431c"

SRC_URI[md5sum] = "a823270b11c8dd7932c12adc6b2fadbb"
SRC_URI[sha256sum] = "4dd9097778224c69228f7f2a0f52e9b13ec2c75e87974ad312525c927835e8fb"

PYPI_PACKAGE = "dbus_next"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
