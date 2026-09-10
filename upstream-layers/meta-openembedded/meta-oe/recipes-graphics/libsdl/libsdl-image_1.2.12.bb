SUMMARY = "Simple DirectMedia Layer image library"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING;md5=613734b7586e1580ef944961c6d62227"

DEPENDS = "tiff zlib libpng jpeg libsdl"

SRC_URI = "http://www.libsdl.org/projects/SDL_image/release/SDL_image-${PV}.tar.gz \
           file://0001-png-img-Fix-prototypes-of-callbacks.patch \
           file://configure.patch"
SRC_URI[sha256sum] = "0b90722984561004de84847744d566809dbb9daf732a9e503b91a1b5a84e5699"

S = "${UNPACKDIR}/SDL_image-${PV}"

inherit autotools pkgconfig

export SDL_CONFIG = "${STAGING_BINDIR_CROSS}/sdl-config"

# configure.in runs AC_PROG_OBJC and libSDL_image_la_SOURCES carries an
# Objective-C file, so automake picks Objective-C as the link language and
# links via $(OBJCLD) = $(OBJC) instead of $(CC). AC_PROG_OBJC detects a bare
# compiler with none of the toolchain options, so the link loses --sysroot and
# the tune flags and then fails to find crtbeginS.o, -lz, -lSDL and friends.
# Point OBJC at the full CC; configure.in already forces OBJCFLAGS=$CFLAGS.
export OBJC = "${CC}"

# Disable the run-time loading of the libs and bring back the soname dependencies.
EXTRA_OECONF += "--disable-jpg-shared --disable-png-shared -disable-tif-shared"

do_configure:prepend() {
    # Removing these files fixes a libtool version mismatch.
    rm -f ${S}/acinclude/libtool.m4
    rm -f ${S}/acinclude/sdl.m4
    rm -f ${S}/acinclude/pkg.m4
    rm -f ${S}/acinclude/lt~obsolete.m4
    rm -f ${S}/acinclude/ltoptions.m4
    rm -f ${S}/acinclude/ltsugar.m4
    rm -f ${S}/acinclude/ltversion.m4
}
