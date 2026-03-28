SUMMARY = "Freetype font rendering library"
DESCRIPTION = "FreeType is a software font engine that is designed to be small, efficient, \
highly customizable, and portable while capable of producing high-quality output (glyph \
images). It can be used in graphics libraries, display servers, font conversion tools, text \
image generation tools, and many other products as well."
HOMEPAGE = "https://freetype.org/"
BUGTRACKER = "https://gitlab.freedesktop.org/groups/freetype/-/issues"
SECTION = "libs"

LICENSE = "(FTL | GPL-2.0-or-later) & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=eeb073d5fb86d23c52bb9b84aa256307 \
                    file://docs/FTL.TXT;md5=72d844cd2f3bcaf6a85244b508032be7 \
                    file://docs/GPLv2.TXT;md5=8ef380476f642c20ebf40fecb0add2ec \
                    "

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/${BPN}/${BP}.tar.xz"
SRC_URI[sha256sum] = "36bc4f1cc413335368ee656c42afca65c5a3987e8768cc28cf11ba775e785a5f"

UPSTREAM_CHECK_REGEX = "freetype-(?P<pver>\d+(\.\d+)+)"

inherit meson pkgconfig multilib_header

PACKAGECONFIG ??= "harfbuzz pixmap zlib"

PACKAGECONFIG[brotli] = "-Dbrotli=enabled,-Dbrotli=disabled,brotli"
PACKAGECONFIG[bzip2] = "-Dbzip2=enabled,-Dbzip2=disabled,bzip2"
PACKAGECONFIG[harfbuzz] = "-Dharfbuzz=dynamic,-Dharfbuzz=disabled"
PACKAGECONFIG[pixmap] = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[zlib] = "-Dzlib=system,-Dzlib=disabled,zlib"

do_install:append() {
	oe_multilib_header freetype2/freetype/config/ftconfig.h
}

BBCLASSEXTEND = "native nativesdk"
