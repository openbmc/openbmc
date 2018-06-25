SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "c25a8c3939e4ad59a5e9e5e1e354832d"
SRC_URI[sha256sum] = "6083d81e46609da8ba80cb826c02d9080764a6dec33c8267ccb7e158833d4c6d"

inherit autotools pkgconfig
