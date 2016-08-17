SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18debddbb3f52c661a129724a883a8e2"

SRC_URI = "https://github.com/gvalkov/python-evdev/archive/v${PV}.zip"

SRC_URI[md5sum] = "24e4ffa98e338b535eae44d91d609005"
SRC_URI[sha256sum] = "61f6893d80da87a995e5781c74d22a39448b1b32004ffac2f31817017709be04"

inherit setuptools
