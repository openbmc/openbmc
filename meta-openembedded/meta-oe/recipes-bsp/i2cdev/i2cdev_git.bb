SUMMARY = "i2c dev tools for Linux"
DESCRIPTION = "\
    This package contains an I2C dev library and the i2c bus scanning \
    utility lsi2c. \
"
HOMEPAGE = "https://github.com/costad2/i2cdev"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=768997ba510a952bef1775c50bc22b00 \
    file://include/libi2cdev.h;beginline=12;endline=25;md5=72486a5e192d6ac5c7e55a4a95e380a6 \
    file://libi2cdev/smbus.c;beginline=9;endline=22;md5=d9a0de5a611b960fa75912ded6c60096 \
    file://lsi2c/lsi2c.c;beginline=11;endline=24;md5=72486a5e192d6ac5c7e55a4a95e380a6 \
"

PV = "0.7.0+git"

SRC_URI = "\
    git://github.com/costad2/i2cdev.git;protocol=https;branch=master \
    file://fix-lsi2c-makefile.patch \
    file://fix-musl.patch \
"
SRCREV = "ed9ad777d842880e7ac6ca5e0de4bd2d3b4d02dc"

S = "${WORKDIR}/git"

inherit autotools
