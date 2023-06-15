SUMMARY = "Freetype font rendering library"
DESCRIPTION = "FreeType is a software font engine that is designed to be small, efficient, \
highly customizable, and portable while capable of producing high-quality output (glyph \
images). It can be used in graphics libraries, display servers, font conversion tools, text \
image generation tools, and many other products as well."
HOMEPAGE = "http://www.freetype.org/"
BUGTRACKER = "https://savannah.nongnu.org/bugs/?group=freetype"
SECTION = "libs"

LICENSE = "FTL | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=a5927784d823d443c6cae55701d01553 \
                    file://docs/FTL.TXT;md5=9f37b4e6afa3fef9dba8932b16bd3f97 \
                    file://docs/GPLv2.TXT;md5=8ef380476f642c20ebf40fecb0add2ec"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/${BPN}/${BP}.tar.xz \ 
           file://CVE-2022-27404.patch \
           file://CVE-2022-27405.patch \
           file://CVE-2022-27406.patch \
           file://CVE-2023-2004.patch \
           "
SRC_URI[sha256sum] = "3333ae7cfda88429c97a7ae63b7d01ab398076c3b67182e960e5684050f2c5c8"

UPSTREAM_CHECK_REGEX = "freetype-(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig multilib_header

# Adapt autotools to work with the minimal autoconf usage in freetype
AUTOTOOLS_SCRIPT_PATH = "${S}/builds/unix"
CONFIGURE_SCRIPT = "${S}/configure"
EXTRA_AUTORECONF += "--exclude=autoheader --exclude=automake"

PACKAGECONFIG ??= "zlib pixmap"

PACKAGECONFIG[bzip2] = "--with-bzip2,--without-bzip2,bzip2"
# harfbuzz results in a circular dependency so enabling is non-trivial
PACKAGECONFIG[harfbuzz] = "--with-harfbuzz,--without-harfbuzz,harfbuzz"
PACKAGECONFIG[pixmap] = "--with-png,--without-png,libpng"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"
PACKAGECONFIG[freetypeconfig] = "--enable-freetype-config=yes,--enable-freetype-config=no,"

EXTRA_OECONF = "CC_BUILD='${BUILD_CC}'"

TARGET_CPPFLAGS += "-D_FILE_OFFSET_BITS=64"

do_install:append() {
	oe_multilib_header freetype2/freetype/config/ftconfig.h
}

BBCLASSEXTEND = "native nativesdk"
