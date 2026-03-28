SUMMARY = "inih (INI Not Invented Here)"
DESCRIPTION = "A .INI file parser written in C that was designed to be small and simple."
HOMEPAGE = "https://github.com/benhoyt/inih"
SECTION = "libs"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a7a95d2af90376e85a05318794e6f202"

SRC_URI = "git://github.com/benhoyt/inih.git;protocol=https;branch=master"

SRCREV = "26254ee9de7681f8825433415443e7116ff24b98"

inherit meson pkgconfig
