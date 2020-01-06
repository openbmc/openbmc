DESCRIPTION = "A module to control Raspberry Pi GPIO channels"
HOMEPAGE = "https://sourceforge.net/projects/raspberry-gpio-python/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=9b95630a648966b142f1a0dcea001cb7"

PYPI_PACKAGE = "RPi.GPIO"
inherit pypi distutils3

SRC_URI += "file://0001-Remove-nested-functions.patch"
SRC_URI[md5sum] = "777617f9dea9a1680f9af43db0cf150e"
SRC_URI[sha256sum] = "7424bc6c205466764f30f666c18187a0824077daf20b295c42f08aea2cb87d3f"

COMPATIBLE_MACHINE = "^rpi$"
