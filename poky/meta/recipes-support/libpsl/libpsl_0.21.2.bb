SUMMARY = "Public Suffix List library"
DESCRIPTION = "The libpsl package provides a library for accessing and \
resolving information from the Public Suffix List (PSL). The PSL is a set of \
domain names beyond the standard suffixes, such as .com."

HOMEPAGE = "https://rockdaboot.github.io/libpsl/"
BUGTRACKER = "https://github.com/rockdaboot/libpsl/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f40ab7fcf5ff18f3ff7f4b0860493fa \
                    file://COPYING;md5=6f40ab7fcf5ff18f3ff7f4b0860493fa \
                    "

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "e35991b6e17001afa2c0ca3b10c357650602b92596209b7492802f3768a6285f"

GITHUB_BASE_URI = "https://github.com/rockdaboot/libpsl/releases"

inherit autotools gettext gtk-doc manpages pkgconfig lib_package github-releases

PACKAGECONFIG ?= "icu"
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native"
PACKAGECONFIG[icu] = "--enable-runtime=libicu --enable-builtin=libicu,,icu"
PACKAGECONFIG[idn2] = "--enable-runtime=libidn2 --enable-builtin=libidn2,,libidn2 libunistring"
BBCLASSEXTEND = "native nativesdk"
