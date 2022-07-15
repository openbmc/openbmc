DESCRIPTION = "Simple DirectMedia Layer networking library."
SECTION = "libs/network"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=fe9d52a78585a65224776875510ed127"

SRC_URI = " \
  https://www.libsdl.org/projects/SDL_net/release/SDL2_net-${PV}.tar.gz \
"
S = "${WORKDIR}/SDL2_net-${PV}"

inherit autotools pkgconfig

DEPENDS = "libsdl2"

SRC_URI[md5sum] = "5c1d9d1cfa63301b141cb5c0de2ea7c4"
SRC_URI[sha256sum] = "15ce8a7e5a23dafe8177c8df6e6c79b6749a03fff1e8196742d3571657609d21"

do_configure:prepend() {
    # create dummy files which autotools consider as mandatory
    touch ${S}/NEWS ${S}/README ${S}/AUTHORS ${S}/ChangeLog  

    # Remove old libtool macros.
    for macro in libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4; do
        echo ${S}/acinclude/macro
        rm -f ${S}/acinclude/$macro
    done
}
