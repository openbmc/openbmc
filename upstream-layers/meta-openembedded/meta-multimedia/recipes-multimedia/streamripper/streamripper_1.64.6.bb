SUMMARY = "download online streams into audio files"
DESCRIPTION = "This command-line tool can be used to record MPEG III \
and OGG online radio-streams into track-separated audio files."
HOMEPAGE = "http://streamripper.sourceforge.net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "glib-2.0 libmad libogg libvorbis"

# While this item does not require it, it depends on libmad which does
LICENSE_FLAGS = "commercial"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
    file://0001-build-these-are-foreign-automake-projects.patch \
    file://0002-build-don-t-ignore-CPPFLAGS-from-environment.patch \
    file://0003-ripstream-fix-compilation.patch \
"
SRC_URI[mdsum] = "a37a1a8b8f9228522196a122a1c2dd32"
SRC_URI[sha256sum] = "c1d75f2e9c7b38fd4695be66eff4533395248132f3cc61f375196403c4d8de42"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-included-argv=yes --with-included-libmad=no"
EXTRA_OECONF += "\
    --with-ogg-includes=${STAGING_INCDIR} \
    --with-ogg-libraries=${STAGING_LIBDIR} \
    --with-vorbis-includes=${STAGING_INCDIR} \
    --with-vorbis-libraries=${STAGING_LIBDIR} \
"

# the included argv library needs this
CPPFLAGS:append = " -DANSI_PROTOTYPES"
