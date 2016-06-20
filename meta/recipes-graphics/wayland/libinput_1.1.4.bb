SUMMARY = "Library to handle input devices in Wayland compositors"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2184aef38ff137ed33ce9a63b9d1eb8f"

DEPENDS = "libevdev udev mtdev"

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BP}.tar.xz \
           file://touchpad-serial-synaptics-need-to-fake-new-touches-on-TRIPLETAP.patch \
"
SRC_URI[md5sum] = "0945318141c1a9f52857bbf65d175f32"
SRC_URI[sha256sum] = "302cb76209b9c57a5a318e178f9bc446eede8ea79386103b5291cbfaa5fab5b6"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[gui] = "--enable-event-gui,--disable-event-gui,cairo gtk+3"

FILES_${PN} += "${libdir}/udev/"
