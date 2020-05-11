SUMMARY = "Python bindings for Linux SPI access through spidev"
DESCRIPTION = "This project contains a python module for interfacing with SPI\
devices from user space via the spidev linux kernel driver.\
This is a modified version of the code originally found\
[here](http://elk.informatik.fh-augsburg.de/da/da-49/trees/pyap7k/lang/py-spi)\
All code is GPLv2 licensed unless explicitly stated otherwise."
HOMEPAGE = "http://github.com/doceme/py-spidev"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRCNAME = "spidev"

SRC_URI[md5sum] = "bbab6352449f1d572cc9eefeafc58bd1"
SRC_URI[sha256sum] = "4314e52f573d95233c907f307558893313a8a606e197e77bb711526b0e179e80"

inherit pypi setuptools3
