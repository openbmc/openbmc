SUMMARY = "Socketcand, socketcan over tcp/ip"
LICENSE = "BSD-3-Clause OR GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://LICENSES/BSD-3-Clause;md5=4c00cf8b0a04a9441d8fa24850231d00 \
    file://LICENSES/GPL-2.0-only.txt;md5=f9d20a453221a1b7e32ae84694da2c37 \
"

SRC_URI = "git://github.com/linux-can/socketcand;branch=master;protocol=https"
SRCREV = "998b0394d028e791aa97d549bfc686b4fbadf5ee"
# Release tags are "0.6.1" (older ones "v0.6.0").
UPSTREAM_CHECK_GITTAGREGEX = "v?(?P<pver>\d+(\.\d+)+)"
# PV is "git" (AUTOINC recipe with no numeric PV), so the resolved tag (0.6.1)
# has nothing to compare against; reported unknown rather than broken.
UPSTREAM_VERSION_UNKNOWN = "1"

inherit meson pkgconfig

PACKAGECONFIG ?= "libconfig libsocketcan"
PACKAGECONFIG[libconfig] = "-Dlibconfig=true,-Dlibconfig=false,libconfig"
PACKAGECONFIG[libsocketcan] = "-Dlibsocketcan=true,-Dlibsocketcan=false,libsocketcan"
