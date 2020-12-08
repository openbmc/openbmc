SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz \
           file://determinism.patch"
SRC_URI[md5sum] = "58286a834ae14536caf9cab8633419cf"
SRC_URI[sha256sum] = "f5603c48c5afd76b14df7a5124e0a94a102f8da0d45826192325069d1bbc7acb"

inherit autotools pkgconfig

UPSTREAM_CHECK_REGEX = "libevdev-(?P<pver>(\d+\.)+(?!90\d+)\d+)"
