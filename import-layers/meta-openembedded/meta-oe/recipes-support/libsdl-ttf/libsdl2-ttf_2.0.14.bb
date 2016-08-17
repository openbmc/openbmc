SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 freetype"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=cb33e7c4df9fbde389f149ad6bc93ce5"

SRC_URI = " \
    http://www.libsdl.org/projects/SDL_ttf/release/SDL2_ttf-${PV}.tar.gz \
    file://use.pkg-config.for.freetype2.patch \
"
SRC_URI[md5sum] = "e53c05e1e7f1382c316afd6c763388b1"
SRC_URI[sha256sum] = "34db5e20bcf64e7071fe9ae25acaa7d72bdc4f11ab3ce59acc768ab62fe39276"

S = "${WORKDIR}/SDL2_ttf-${PV}"

inherit autotools

do_configure_prepend() {
    touch ${S}/NEWS ${S}/README ${S}/AUTHORS ${S}/ChangeLog

    # Removing these files fixes a libtool version mismatch.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"

    for i in ${MACROS}; do
        rm -f ${S}/acinclude/$i
    done
}
