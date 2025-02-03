SUMMARY = "Simple DirectMedia Layer image library v2"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2f6d9e01c97958aa851954ed5acf82ee"

DEPENDS = "tiff zlib libpng jpeg libsdl2 libwebp"

SRC_URI = "http://www.libsdl.org/projects/SDL_image/release/SDL2_image-${PV}.tar.gz"
SRC_URI[sha256sum] = "5a89a01420a192b89dbcc5f5267448181d5dcc81d2f5a1688cb1eac6f557da67"

S = "${WORKDIR}/SDL2_image-${PV}"

inherit autotools pkgconfig

# Disable the run-time loading of the libs and bring back the soname dependencies.
EXTRA_OECONF += "--disable-jpg-shared --disable-png-shared -disable-tif-shared"

do_configure:prepend() {
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
