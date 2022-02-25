SUMMARY = "Library for reading extended image information (EXIF) from JPEG files"
DESCRIPTION = "libexif is a library for parsing, editing, and saving EXIF data. It is \
intended to replace lots of redundant implementations in command-line \
utilities and programs with GUIs."
HOMEPAGE = "https://libexif.github.io/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

def version_underscore(v):
    return "_".join(v.split("."))

SRC_URI = "https://github.com/libexif/libexif/releases/download/v${PV}/libexif-${PV}.tar.bz2 \
           "

SRC_URI[sha256sum] = "d47564c433b733d83b6704c70477e0a4067811d184ec565258ac563d8223f6ae"

UPSTREAM_CHECK_URI = "https://github.com/libexif/libexif/releases/"

inherit autotools gettext

EXTRA_OECONF += "--disable-docs"

BBCLASSEXTEND = "native nativesdk"
