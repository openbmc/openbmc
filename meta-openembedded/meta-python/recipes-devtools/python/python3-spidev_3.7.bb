SUMMARY = "Python bindings for Linux SPI access through spidev"
DESCRIPTION = "This project contains a python module for interfacing with SPI\
devices from user space via the spidev linux kernel driver.\
This is a modified version of the code originally found\
[here](http://elk.informatik.fh-augsburg.de/da/da-49/trees/pyap7k/lang/py-spi)\
All code is GPLv2 licensed unless explicitly stated otherwise."
HOMEPAGE = "https://github.com/doceme/py-spidev"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2077511c543a7c85245a516c47f4de78"
SRCNAME = "spidev"

SRC_URI[sha256sum] = "ce628a5ff489f45132679879bff5f455a66abf9751af01843850155b06ae92f0"

inherit pypi python_setuptools_build_meta
