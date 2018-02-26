SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "4f1cfaee8d75ea3fbbfeb99a98730952"
SRC_URI[sha256sum] = "a1e59e37a2f0d397ffd7e83b73af0e638db83b8dd08902ef0f651a21cc1dd422"

inherit autotools pkgconfig
