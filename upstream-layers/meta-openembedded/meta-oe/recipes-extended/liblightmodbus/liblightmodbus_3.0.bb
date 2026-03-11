SUMMARY = "A cross-platform, lightweight Modbus RTU library"
DESCRIPTION = "liblightmodbus is a very lightweight, highly configurable, \
	       platform-independent Modbus RTU library."

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI = "git://github.com/Jacajack/liblightmodbus.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "e7be88bc65abec4a902f4e5194d7235ebd3a19aa"

do_install() {
        install -d ${D}${includedir}/lightmodbus
        install -m 0644 ${S}/include/lightmodbus/*.h ${D}${includedir}/lightmodbus/

        install -d ${D}${libdir}/cmake/lightmodbus
        install -m 0644 ${S}/lightmodbusConfig.cmake ${D}${libdir}/cmake/lightmodbus/
}
