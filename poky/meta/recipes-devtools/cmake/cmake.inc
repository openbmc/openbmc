# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

SUMMARY = "Cross-platform, open-source make system"
DESCRIPTION = "CMake is used to control the software compilation process \
using simple platform and compiler independent configuration files. CMake \
generates native makefiles and workspaces that can be used in the compiler \
environment of your choice."
HOMEPAGE = "http://www.cmake.org/"
BUGTRACKER = "http://public.kitware.com/Bug/my_view_page.php"
SECTION = "console/utils"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://Copyright.txt;md5=c721f56fce89ba2eadc2fdd8ba1f4d83 \
                    file://Source/cmake.h;beginline=1;endline=3;md5=4494dee184212fc89c469c3acd555a14 \
                    "

CMAKE_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_URI = "https://cmake.org/files/v${CMAKE_MAJOR_VERSION}/cmake-${PV}.tar.gz \
           file://0002-cmake-Prevent-the-detection-of-Qt5.patch \
           file://0003-cmake-support-OpenEmbedded-Qt4-tool-binary-names.patch \
           file://0004-Fail-silently-if-system-Qt-installation-is-broken.patch \
"

SRC_URI[sha256sum] = "5d4e40fc775d3d828c72e5c45906b4d9b59003c9433ff1b36a1cb552bbd51d7e"

UPSTREAM_CHECK_REGEX = "cmake-(?P<pver>\d+(\.\d+)+)\.tar"

# This is specific to the npm package that installs cmake, so isn't
# relevant to OpenEmbedded
CVE_CHECK_WHITELIST += "CVE-2016-10642"
