DESCRIPTION = "Zlog is a pure C logging library"
HOMEPAGE = "https://github.com/HardySimpson/zlog"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/HardySimpson/zlog;branch=master;protocol=https;tag=${PV}"
SRCREV = "728602097c5f07f860f7ca32baecb229d89a2637"

inherit cmake

