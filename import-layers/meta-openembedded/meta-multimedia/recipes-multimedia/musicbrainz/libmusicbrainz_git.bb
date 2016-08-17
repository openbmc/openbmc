SUMMARY = "MusicBrainz client library"
DESCRIPTION = "The MusicBrainz client is a library which can be built into other programs.  The library allows you to access the data held on the MusicBrainz server."
HOMEPAGE = "http://musicbrainz.org"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=fbc093901857fcd118f065f900982c24"
DEPENDS = "expat neon neon-native"

PV = "5.0.1+git${SRCPV}"
PR = "r1"

SRCREV = "0749dd0a35b4a54316da064475863a4ac6e28e7e"
SRC_URI = "git://github.com/metabrainz/libmusicbrainz.git \
           file://allow-libdir-override.patch "

S = "${WORKDIR}/git"

LDFLAGS_prepend_libc-uclibc = " -lpthread "

inherit cmake pkgconfig

do_configure_prepend() {
    # The native build really doesn't like being rebuilt, so delete
    # it if it's already present. Also delete all other files not
    # known to Git to fix subsequent invocations of do_configure.
    git clean -dfx -e /.pc/ -e /patches/ .
    mkdir build-native
    cd build-native
    cmake -DCMAKE_C_FLAGS=${BUILD_CFLAGS} \
          -DCMAKE_C_COMPILER=${BUILD_CC} \
          -DCMAKE_CXX_FLAGS=${BUILD_CXXFLAGS} \
          -DCMAKE_CXX_COMPILER=${BUILD_CXX} \
          ..
    make make-c-interface
    cd ..
}

EXTRA_OECMAKE = "-DLIB_INSTALL_DIR:PATH=${libdir} \
                 -DIMPORT_EXECUTABLES=build-native/ImportExecutables.cmake"

# out-of-tree building doesn't appear to work for this package.
B = "${S}"
