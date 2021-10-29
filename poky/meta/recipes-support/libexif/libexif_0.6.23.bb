SUMMARY = "Library for reading extended image information (EXIF) from JPEG files"
DESCRIPTION = "libexif is a library for parsing, editing, and saving EXIF data. It is \
intended to replace lots of redundant implementations in command-line \
utilities and programs with GUIs."
HOMEPAGE = "https://libexif.github.io/"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

def version_underscore(v):
    return "_".join(v.split("."))

SRC_URI = "https://github.com/libexif/libexif/releases/download/v${PV}/libexif-${PV}.tar.xz \
           "

SRC_URI[sha256sum] = "a740a99920eb81ae0aa802bb46e683ce6e0cde061c210f5d5bde5b8572380431"

UPSTREAM_CHECK_URI = "https://github.com/libexif/libexif/releases/"

inherit autotools gettext

EXTRA_OECONF += "--disable-docs"

BBCLASSEXTEND = "native nativesdk"
