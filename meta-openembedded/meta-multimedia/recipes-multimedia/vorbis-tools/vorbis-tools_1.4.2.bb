SUMMARY = "Vorbis Tools"
DESCRIPTION = "Ogg Vorbis is a high-quality lossy audio codec \
that is free of intellectual property restrictions. vorbis-tools \
include some command line applications to use the libraries."
HOMEPAGE = "http://www.vorbis.com/"
BUGTRACKER = "https://trac.xiph.org"
SECTION = "multimedia"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libogg libvorbis"

SRC_URI = "http://downloads.xiph.org/releases/vorbis/${BP}.tar.gz \
           file://gettext.patch \
           file://0001-ogginfo-Include-utf8.h-for-missing-utf8_decode.patch \
          "

SRC_URI[md5sum] = "998fca293bd4e4bdc2b96fb70f952f4e"
SRC_URI[sha256sum] = "db7774ec2bf2c939b139452183669be84fda5774d6400fc57fde37f77624f0b0"

inherit autotools pkgconfig gettext

PACKAGECONFIG ??= "flac ogg123"
PACKAGECONFIG[flac] = ",--without-flac,flac,libflac"
PACKAGECONFIG[speex] = ",--without-speex,speex,speex"
PACKAGECONFIG[ogg123] = "--enable-ogg123,--disable-ogg123,libao curl"
