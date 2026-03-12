DESCRIPTION = "Zlog is a pure C logging library"
HOMEPAGE = "https://github.com/HardySimpson/zlog"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/HardySimpson/zlog;branch=master;protocol=https;tag=${PV} \
           file://upgrade_cmake_minimum_version.patch \
           file://0001-Don-t-overwrite-systemwide-cflags.patch"
SRCREV = "7fe61ca6265516e9327a51fc394b2adb126c2ef3"

inherit cmake

