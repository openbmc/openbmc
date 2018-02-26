DESCRIPTION = "A module to control Raspberry Pi GPIO channels"
HOMEPAGE = "http://code.google.com/p/raspberry-gpio-python/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=9b95630a648966b142f1a0dcea001cb7"

PYPI_PACKAGE = "RPi.GPIO"
inherit pypi distutils

SRC_URI += "file://0001-Remove-nested-functions.patch"
SRC_URI[md5sum] = "e4abe1cfb5eacebe53078032256eb837"
SRC_URI[sha256sum] = "a5fc0eb5e401963b6c0a03650da6b42c4005f02d962b81241d96c98d0a578516"

COMPATIBLE_MACHINE = "^rpi$"
