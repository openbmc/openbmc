SUMMARY = "Include What You Use (IWYU) - Clang based checker for C/C++ header includes"
DESCRIPTION = "For every symbol (type, function, variable, or macro) that you \
               use in foo.cc (or foo.cpp), either foo.cc or foo.h should \
               include a .h file that exports the declaration of that symbol."
HOMEPAGE = "https://include-what-you-use.org"
BUGTRACKER = "https://github.com/include-what-you-use/include-what-you-use/issues"
LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=3bb66a14534286912cd6f26649b5c60a \
                    file://iwyu-check-license-header.py;md5=7bdb749831163fbe9232b3cb7186116f"

DEPENDS = "clang"

PV .= "+git"
SRCREV = "791e69ea4662cb3e74e8128fd5fd69bd7f4ea6b3"
SRC_URI = "git://github.com/include-what-you-use/include-what-you-use.git;protocol=https;branch=clang_21"

inherit cmake python3native

EXTRA_OECMAKE = "-DIWYU_RESOURCE_RELATIVE_TO=iwyu"

FILES:${PN} += "${datadir}/${BPN}"

BBCLASSEXTEND = "nativesdk"
