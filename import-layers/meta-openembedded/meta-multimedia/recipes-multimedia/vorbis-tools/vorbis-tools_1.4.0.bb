SUMMARY = "Vorbis Tools"
DESCRIPTION = "Ogg Vorbis is a high-quality lossy audio codec \
that is free of intellectual property restrictions. vorbis-tools \
include some command line applications to use the libraries."
HOMEPAGE = "http://www.vorbis.com/"
BUGTRACKER = "https://trac.xiph.org"
SECTION = "multimedia"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libogg libvorbis curl libao"

SRC_URI = "http://downloads.xiph.org/releases/vorbis/${BP}.tar.gz \
           file://0001-oggenc-Fix-large-alloca-on-bad-AIFF-input.patch \
          "

SRC_URI[md5sum] = "567e0fb8d321b2cd7124f8208b8b90e6"
SRC_URI[sha256sum] = "a389395baa43f8e5a796c99daf62397e435a7e73531c9f44d9084055a05d22bc"

inherit autotools pkgconfig gettext

PACKAGECONFIG ??= "flac"
PACKAGECONFIG[flac] = ",--without-flac,flac,libflac"
PACKAGECONFIG[speex] = ",--without-speex,speex,speex"
