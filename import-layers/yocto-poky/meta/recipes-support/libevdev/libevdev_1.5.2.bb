SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "96ad6edb5ce5c4428f1b183cc180ff8c"
SRC_URI[sha256sum] = "5ee2163656a61f5703cb5c08a05c9471ffb7b640bfbe2c55194ea50d908f629b"

inherit autotools pkgconfig
