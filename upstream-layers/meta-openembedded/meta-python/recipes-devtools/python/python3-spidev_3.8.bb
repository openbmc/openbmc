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

SRC_URI[sha256sum] = "2bc02fb8c6312d519ebf1f4331067427c0921d3f77b8bcaf05189a2e8b8382c0"

inherit pypi python_setuptools_build_meta
