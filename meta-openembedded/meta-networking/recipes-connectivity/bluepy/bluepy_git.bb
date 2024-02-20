DESCRIPTION = "Python interface to Bluetooth LE on Linux"
HOMEPAGE = "https://github.com/IanHarvey/bluepy"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only & PD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=59e0d45ea684dda215889aa1b5acd001"
DEPENDS = "glib-2.0"
SRCREV = "7ad565231a97c304c0eff45f2649cd005e69db09"
PV = "1.3.0+git"

SRC_URI = "git://github.com/IanHarvey/bluepy.git;protocol=https;branch=master \
           file://0001-bluepy-Fix-username-issue-with-tarballs.patch \
          "

S = "${WORKDIR}/git"

inherit setuptools3 pkgconfig

RDEPENDS:${PN} = "bluez5"

TARGET_CC_ARCH += "${LDFLAGS}"
