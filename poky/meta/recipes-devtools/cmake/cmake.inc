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
LIC_FILES_CHKSUM = "file://Copyright.txt;md5=622747147b46f22e1953876a7cba3323 \
                    file://Source/cmake.h;md5=4494dee184212fc89c469c3acd555a14;beginline=1;endline=3 \
                    "

CMAKE_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_URI = "https://cmake.org/files/v${CMAKE_MAJOR_VERSION}/cmake-${PV}.tar.gz \
           file://0002-cmake-Prevent-the-detection-of-Qt5.patch \
           file://0003-cmake-support-OpenEmbedded-Qt4-tool-binary-names.patch \
           file://0004-Fail-silently-if-system-Qt-installation-is-broken.patch \
"

SRC_URI[md5sum] = "d86ccaf3d2462b6b5947919abe5b9f15"
SRC_URI[sha256sum] = "5f760b50b8ecc9c0c37135fae5fbf00a2fef617059aa9d61c1bb91653e5a8bfc"

UPSTREAM_CHECK_REGEX = "cmake-(?P<pver>\d+(\.\d+)+)\.tar"
