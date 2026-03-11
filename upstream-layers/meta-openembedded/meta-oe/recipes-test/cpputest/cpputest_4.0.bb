SUMMARY = "CppUTest unit testing and mocking framework for C/C++"
HOMEPAGE = "http://cpputest.github.io/"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce5d5f1fe02bcd1343ced64a06fd4177"

SRC_URI = "git://github.com/cpputest/cpputest.git;protocol=https;branch=master \
           file://0001-Call-cmake_minimum_required-before-project-in-CMakeL.patch \
           file://0002-Squelch-deprecation-warning.patch \
"
SRCREV = "67d2dfd41e13f09ff218aa08e2d35f1c32f032a1"


inherit cmake

EXTRA_OECMAKE = "-DLONGLONG=ON \
                 -DC++11=ON \
                 -DTESTS=OFF \
                 "

DEV_PKG_DEPENDENCY = ""

FILES:${PN}-dev += "${libdir}/CppUTest/cmake/*"

PACKAGECONFIG ??= ""
PACKAGECONFIG[extensions] = "-DEXTENSIONS=ON,-DEXTENSIONS=OFF"
