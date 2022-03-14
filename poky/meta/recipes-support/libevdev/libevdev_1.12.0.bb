SUMMARY = "Wrapper library for evdev devices"
DESCRIPTION = "A library for handling evdev kernel devices. It abstracts \
the evdev ioctls through type-safe interfaces and provides functions \
to change the appearance of the device."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=80c550b3197bcb8da7d7557ebcc3fc46 \
                    "

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"
SRC_URI[sha256sum] = "2f729e3480695791f9482e8388bd723402b89f0eaf118057bbdea3cecee9b237"

inherit autotools pkgconfig

UPSTREAM_CHECK_REGEX = "libevdev-(?P<pver>(\d+\.)+(?!90\d+)\d+)"
