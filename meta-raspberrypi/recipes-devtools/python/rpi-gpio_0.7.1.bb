DESCRIPTION = "A module to control Raspberry Pi GPIO channels"
HOMEPAGE = "https://sourceforge.net/projects/raspberry-gpio-python/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=a2294b0b1daabc30dfb5b3de73b2e00a"

PYPI_PACKAGE = "RPi.GPIO"

inherit pypi setuptools3

SRC_URI += "file://0001-Remove-nested-functions.patch \
           "
SRC_URI[sha256sum] = "cd61c4b03c37b62bba4a5acfea9862749c33c618e0295e7e90aa4713fb373b70"

COMPATIBLE_MACHINE = "^rpi$"
