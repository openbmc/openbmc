SUMMARY = "A cross-platform, lightweight Modbus RTU library"
DESCRIPTION = "liblightmodbus is a very lightweight, highly configurable, \
	       platform-independent Modbus RTU library."

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

inherit cmake pkgconfig

SRC_URI = "git://github.com/Jacajack/liblightmodbus.git;protocol=https \
           file://0001-cmake-Use-GNUInstallDirs-instead-of-hardcoding-lib-p.patch \
          "
SRCREV = "59d2b405f95701e5b04326589786dbb43ce49e81"

S = "${WORKDIR}/git"
