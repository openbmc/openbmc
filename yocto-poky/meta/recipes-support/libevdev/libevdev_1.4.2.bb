SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"
SECTION = "libs"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

SRC_URI = "http://www.freedesktop.org/software/libevdev/${BP}.tar.xz"

SRC_URI[md5sum] = "debfcd5153558eba25a8e71eb2534ae8"
SRC_URI[sha256sum] = "7c77b213f075706d428a2af54b66bf86b03a68e9fb390e7ec48cf55065970643"

inherit autotools pkgconfig
