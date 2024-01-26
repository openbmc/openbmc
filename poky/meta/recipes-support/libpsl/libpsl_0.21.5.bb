SUMMARY = "Public Suffix List library"
DESCRIPTION = "The libpsl package provides a library for accessing and \
resolving information from the Public Suffix List (PSL). The PSL is a set of \
domain names beyond the standard suffixes, such as .com."

HOMEPAGE = "https://rockdaboot.github.io/libpsl/"
BUGTRACKER = "https://github.com/rockdaboot/libpsl/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9f9e317096db2a598fc44237c5b8a4f7 \
                    file://COPYING;md5=9f9e317096db2a598fc44237c5b8a4f7 \
                    "

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "1dcc9ceae8b128f3c0b3f654decd0e1e891afc6ff81098f227ef260449dae208"

GITHUB_BASE_URI = "https://github.com/rockdaboot/libpsl/releases"

inherit autotools gettext gtk-doc manpages pkgconfig lib_package github-releases

PACKAGECONFIG ?= "icu"
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native"
PACKAGECONFIG[icu] = "--enable-runtime=libicu --enable-builtin=libicu,,icu"
PACKAGECONFIG[idn2] = "--enable-runtime=libidn2 --enable-builtin=libidn2,,libidn2 libunistring"
BBCLASSEXTEND = "native nativesdk"
