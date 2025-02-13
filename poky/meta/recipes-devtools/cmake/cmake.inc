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
LIC_FILES_CHKSUM = "file://Copyright.txt;md5=718f05155941b33862726348d3cd46ce \
                    file://Source/cmake.h;beginline=1;endline=2;md5=a5f70e1fef8614734eae0d62b4f5891b \
                    "

CMAKE_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_URI = "https://cmake.org/files/v${CMAKE_MAJOR_VERSION}/cmake-${PV}.tar.gz \
           file://0001-CMakeDetermineCompilerABI-Strip-pipe-from-compile-fl.patch \
           "

SRC_URI[sha256sum] = "66fb53a145648be56b46fa9e8ccade3a4d0dfc92e401e52ce76bdad1fea43d27"

UPSTREAM_CHECK_REGEX = "cmake-(?P<pver>\d+(\.\d+)+)\.tar"

CVE_STATUS[CVE-2016-10642] = "cpe-incorrect: This is specific to the npm package that installs cmake, so isn't relevant to OpenEmbedded"
