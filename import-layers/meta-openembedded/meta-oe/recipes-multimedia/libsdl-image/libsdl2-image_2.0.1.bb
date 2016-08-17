SUMMARY = "Simple DirectMedia Layer image library v2"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=ec65b9778f5584a0bd8dfc17d6340ba0"

DEPENDS = "tiff zlib libpng jpeg virtual/libsdl2 libwebp"

SRC_URI = "http://www.libsdl.org/projects/SDL_image/release/SDL2_image-${PV}.tar.gz"
SRC_URI[md5sum] = "d94b94555ba022fa249a53a021dc3606"
SRC_URI[sha256sum] = "3a3eafbceea5125c04be585373bfd8b3a18f259bd7eae3efc4e6d8e60e0d7f64"

S = "${WORKDIR}/SDL2_image-${PV}"

inherit autotools pkgconfig

# Disable the run-time loading of the libs and bring back the soname dependencies.
EXTRA_OECONF += "--disable-jpg-shared --disable-png-shared -disable-tif-shared"

do_configure_prepend() {
    # make autoreconf happy
    touch ${S}/NEWS ${S}/README ${S}/AUTHORS ${S}/ChangeLog
    # Removing these files fixes a libtool version mismatch.
    rm -f ${S}/acinclude/libtool.m4
    rm -f ${S}/acinclude/sdl2.m4
    rm -f ${S}/acinclude/pkg.m4
    rm -f ${S}/acinclude/lt~obsolete.m4
    rm -f ${S}/acinclude/ltoptions.m4
    rm -f ${S}/acinclude/ltsugar.m4
    rm -f ${S}/acinclude/ltversion.m4
}
