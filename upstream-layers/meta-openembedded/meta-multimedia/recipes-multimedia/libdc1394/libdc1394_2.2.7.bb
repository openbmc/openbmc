SUMMARY = "Capture and control API for IIDC compliant cameras"
HOMEPAGE = "http://sourceforge.net/projects/libdc1394/"
SECTION = "libs"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c848e78d9a4a5cc69906178e4d6fbd64"

# libsdl to provide sdl.m4 with AM_PATH_SDL
DEPENDS += "libusb1 libraw1394 libsdl \
            ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxv virtual/libx11', '', d)} \
            ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'virtual/egl', '', d)} \
            ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'libglu', '', d)} \
"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://install_examples.patch \
          "
SRC_URI[sha256sum] = "537ceb78dd3cef271a183f4a176191d1cecf85f025520e6bd3758b0e19e6609f"
inherit autotools pkgconfig

EXTRA_OECONF += "--disable-doxygen-doc --disable-examples"
