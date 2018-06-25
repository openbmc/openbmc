# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

SUMMARY = "Cross-platform, open-source make system"
HOMEPAGE = "http://www.cmake.org/"
BUGTRACKER = "http://public.kitware.com/Bug/my_view_page.php"
SECTION = "console/utils"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://Copyright.txt;md5=79b5b78197c74d5c5f9c7ccadeee4e8c \
                    file://Source/cmake.h;md5=4494dee184212fc89c469c3acd555a14;beginline=1;endline=3 \
                    "

CMAKE_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_URI = "https://cmake.org/files/v${CMAKE_MAJOR_VERSION}/cmake-${PV}.tar.gz \
           file://support-oe-qt4-tools-names.patch \
           file://qt4-fail-silent.patch \
           file://cmake-Prevent-the-detection-of-Qt5.patch \
           file://0001-FindBoost-Boost-1.66.0-dependency-and-release-update.patch \
           file://0001-FindBoost-Fix-incorrect-alphabetisation-of-headers-l.patch \
           file://0002-FindBoost-Implement-Architecture-and-Address-Model-t.patch \
           file://0003-FindBoost-Search-for-upstream-packaged-libs-next-to-.patch \
           "

SRC_URI[md5sum] = "1c38c67295ca696aeafd8c059d748b38"
SRC_URI[sha256sum] = "0c3a1dcf0be03e40cf4f341dda79c96ffb6c35ae35f2f911845b72dab3559cf8"

UPSTREAM_CHECK_REGEX = "cmake-(?P<pver>\d+(\.\d+)+)\.tar"
