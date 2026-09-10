SUMMARY = "Wrapper library for evdev devices"
DESCRIPTION = "A library for handling evdev kernel devices. It abstracts \
the evdev ioctls through type-safe interfaces and provides functions \
to change the appearance of the device."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=80c550b3197bcb8da7d7557ebcc3fc46"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"
SRC_URI[sha256sum] = "0caf824971108f15bb2ad356433bae198d7d3bf1e82d43f63626e069e060bfa6"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=disabled -Ddocumentation=disabled"

UPSTREAM_CHECK_REGEX = "libevdev-(?P<pver>(\d+\.)+(?!90\d+)\d+)"
