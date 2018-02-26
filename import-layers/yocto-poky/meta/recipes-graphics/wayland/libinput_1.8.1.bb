SUMMARY = "Library to handle input devices in Wayland compositors"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2184aef38ff137ed33ce9a63b9d1eb8f"

DEPENDS = "libevdev udev mtdev"

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BP}.tar.xz \
           file://0001-tools-Fix-race-in-autotools-install.patch \
"

SRC_URI[md5sum] = "8247f0bb67052ffb272c50c3cb9c5998"
SRC_URI[sha256sum] = "e3590a9037e561a5791c8bd3b34bfd30fad5cacd8cbefc0d75fafe3a41d07147"

inherit autotools pkgconfig lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[libwacom] = "--enable-libwacom,--disable-libwacom,libwacom"
PACKAGECONFIG[gui] = "--enable-debug-gui,--disable-debug-gui,cairo gtk+3"

UDEVDIR = "`pkg-config --variable=udevdir udev`"

EXTRA_OECONF += "--with-udev-dir=${UDEVDIR} --disable-documentation --disable-tests"

# package name changed in 1.8.1 upgrade: make sure package upgrades work
RPROVIDES_${PN} = "libinput"
RREPLACES_${PN} = "libinput"
RCONFLICTS_${PN} = "libinput"
