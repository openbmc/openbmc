SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "fabe87d8d7d7f7cdb4a26e5dc99fc517"
SRC_URI[sha256sum] = "11dbe1f2b1d03a51f3e9a196757a75c3a999042ce34cf1fdc00a2363e5a2e369"

inherit autotools pkgconfig
