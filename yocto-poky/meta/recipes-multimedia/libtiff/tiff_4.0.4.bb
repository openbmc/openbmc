SUMMARY = "Provides support for the Tag Image File Format (TIFF)"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=34da3db46fab7501992f9615d7e158cf"
HOMEPAGE = "http://www.remotesensing.org/libtiff/"

SRC_URI = "ftp://ftp.remotesensing.org/pub/libtiff/tiff-${PV}.tar.gz \
           file://libtool2.patch \
          "

SRC_URI[md5sum] = "9aee7107408a128c0c7b24286c0db900"
SRC_URI[sha256sum] = "8cb1d90c96f61cdfc0bcf036acc251c9dbe6320334da941c7a83cfe1576ef890"

inherit autotools

CACHED_CONFIGUREVARS = "ax_cv_check_gl_libgl=no"

PACKAGECONFIG ?= "cxx jpeg zlib lzma \
                  strip-chopping extrasample-as-alpha check-ycbcr-subsampling"

PACKAGECONFIG[cxx] = "--enable-cxx,--disable-cxx,,"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg,"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib,"
PACKAGECONFIG[lzma] = "--enable-lzma,--disable-lzma,xz,"

# Convert single-strip uncompressed images to multiple strips of specified
# size (default: 8192) to reduce memory usage
PACKAGECONFIG[strip-chopping] = "--enable-strip-chopping,--disable-strip-chopping,,"

# Treat a fourth sample with no EXTRASAMPLE_ value as being ASSOCALPHA
PACKAGECONFIG[extrasample-as-alpha] = "--enable-extrasample-as-alpha,--disable-extrasample-as-alpha,,"

# Control picking up YCbCr subsample info. Disable to support files lacking
# the tag
PACKAGECONFIG[check-ycbcr-subsampling] = "--enable-check-ycbcr-subsampling,--disable-check-ycbcr-subsampling,,"

# Support a mechanism allowing reading large strips (usually one strip files)
# in chunks when using TIFFReadScanline. Experimental 4.0+ feature
PACKAGECONFIG[chunky-strip-read] = "--enable-chunky-strip-read,--disable-chunky-strip-read,,"

PACKAGES =+ "tiffxx tiff-utils"
FILES_tiffxx = "${libdir}/libtiffxx.so.*"
FILES_tiff-utils = "${bindir}/*"

BBCLASSEXTEND = "native"
