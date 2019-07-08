SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "154b24f01425c4c82fdc3e11f2c13af6"
SRC_URI[sha256sum] = "f5005c865987d980cc1279b9ec6131b06a89fd9892f649f2a68262b8786ef814"

inherit autotools pkgconfig
