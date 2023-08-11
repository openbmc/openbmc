# Released under the MIT license.

SUMMARY = "Mimetic Library for multi-part parsing"
DESCRIPTION = "Email library (MIME) written in C++ designed to be easy to use and integrate but yet fast and efficient."
HOMEPAGE = "http://www.codesink.org/mimetic_mime_library.html"
BUGTRACKER = "https://github.com/LadislavSopko/mimetic/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b49da7df0ca479ef01ff7f2d799eabee"

SRCREV = "50486af99b4f9b35522d7b3de40b6ce107505279"
SRC_URI += "git://github.com/LadislavSopko/mimetic/;branch=master;protocol=https \
            file://0001-libmimetic-Removing-test-directory-from-the-Makefile.patch \
            file://0001-mimetic-Check-for-MMAP_FAILED-return-from-mmap.patch \
           "

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CXXFLAGS += "-Wno-narrowing -std=c++14"

inherit autotools
