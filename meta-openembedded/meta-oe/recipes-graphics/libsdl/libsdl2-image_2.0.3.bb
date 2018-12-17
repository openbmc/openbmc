SUMMARY = "Simple DirectMedia Layer image library v2"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=a855a9eb5507fc556e672080c4b428ca"

DEPENDS = "tiff zlib libpng jpeg virtual/libsdl2 libwebp"

SRC_URI = "http://www.libsdl.org/projects/SDL_image/release/SDL2_image-${PV}.tar.gz"
SRC_URI[md5sum] = "c6baf6dfa80fa8a66853661a36a6034e"
SRC_URI[sha256sum] = "3510c25da735ffcd8ce3b65073150ff4f7f9493b866e85b83738083b556d2368"

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
