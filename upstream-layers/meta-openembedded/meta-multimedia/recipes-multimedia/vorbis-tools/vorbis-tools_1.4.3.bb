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
          "

SRC_URI[sha256sum] = "a1fe3ddc6777bdcebf6b797e7edfe0437954b24756ffcc8c6b816b63e0460dde"

inherit autotools pkgconfig gettext

PACKAGECONFIG ??= "flac ogg123"
PACKAGECONFIG[flac] = ",--without-flac,flac,libflac"
PACKAGECONFIG[speex] = ",--without-speex,speex,speex"
PACKAGECONFIG[ogg123] = "--enable-ogg123,--disable-ogg123,libao curl"
