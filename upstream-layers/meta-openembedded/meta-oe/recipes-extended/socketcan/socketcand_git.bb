SUMMARY = "Socketcand, socketcan over tcp/ip"
LICENSE = "GPL-2.0-only | BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSES/BSD-3-Clause;md5=4c00cf8b0a04a9441d8fa24850231d00 \
    file://LICENSES/GPL-2.0-only.txt;md5=f9d20a453221a1b7e32ae84694da2c37 \
"

SRC_URI = "git://github.com/linux-can/socketcand;branch=master;protocol=https"
SRCREV = "998b0394d028e791aa97d549bfc686b4fbadf5ee"

inherit meson pkgconfig

PACKAGECONFIG ?= "libconfig libsocketcan"
PACKAGECONFIG[libconfig] = "-Dlibconfig=true,-Dlibconfig=false,libconfig"
PACKAGECONFIG[libsocketcan] = "-Dlibsocketcan=true,-Dlibsocketcan=false,libsocketcan"
