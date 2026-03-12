SUMMARY = "CppUTest unit testing and mocking framework for C/C++"
HOMEPAGE = "http://cpputest.github.io/"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce5d5f1fe02bcd1343ced64a06fd4177"

SRC_URI = "git://github.com/cpputest/cpputest.git;protocol=https;branch=master"
SRCREV = "aa49f2bfef314715c7c12c806bb44f3b85266d60"

PV = "4.0+git"

inherit cmake

EXTRA_OECMAKE = "-DCPPUTEST_USE_LONGLONG=ON"

DEV_PKG_DEPENDENCY = ""

FILES:${PN}-dev += "${libdir}/CppUTest/cmake/*"

PACKAGECONFIG ??= ""
PACKAGECONFIG[extensions] = "-DCPPUTEST_EXTENSIONS=ON,-DCPPUTEST_EXTENSIONS=OFF"
