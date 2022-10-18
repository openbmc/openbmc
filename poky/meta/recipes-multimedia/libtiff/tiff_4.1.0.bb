SUMMARY = "Provides support for the Tag Image File Format (TIFF)"
DESCRIPTION = "Library provides support for the Tag Image File Format \
(TIFF), a widely used format for storing image data.  This library \
provide means to easily access and create TIFF image files."
HOMEPAGE = "http://www.libtiff.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=34da3db46fab7501992f9615d7e158cf"

CVE_PRODUCT = "libtiff"

SRC_URI = "http://download.osgeo.org/libtiff/tiff-${PV}.tar.gz \
           file://CVE-2020-35523.patch  \
           file://CVE-2020-35524-1.patch \
           file://CVE-2020-35524-2.patch \
           file://001_support_patch_for_CVE-2020-35521_and_CVE-2020-35522.patch \
           file://002_support_patch_for_CVE-2020-35521_and_CVE-2020-35522.patch \
           file://CVE-2020-35521_and_CVE-2020-35522.patch \
           file://0001-tiffset-fix-global-buffer-overflow-for-ASCII-tags-wh.patch \
           file://561599c99f987dc32ae110370cfdd7df7975586b.patch \
           file://eecb0712f4c3a5b449f70c57988260a667ddbdef.patch \
           file://CVE-2022-0865.patch \
           file://CVE-2022-0908.patch \
           file://CVE-2022-0907.patch \
           file://CVE-2022-0909.patch \
           file://CVE-2022-0891.patch \
           file://CVE-2022-0924.patch \
           file://CVE-2022-2056-CVE-2022-2057-CVE-2022-2058.patch \
           file://CVE-2022-34526.patch \
           file://CVE-2022-2867-CVE-2022-2868-CVE-2022-2869.patch \
           file://CVE-2022-1354.patch \
           file://CVE-2022-1355.patch \
          "
SRC_URI[md5sum] = "2165e7aba557463acc0664e71a3ed424"
SRC_URI[sha256sum] = "5d29f32517dadb6dbcd1255ea5bbc93a2b54b94fbf83653b4d65c7d6775b8634"

# exclude betas
UPSTREAM_CHECK_REGEX = "tiff-(?P<pver>\d+(\.\d+)+).tar"

# Tested with check from https://security-tracker.debian.org/tracker/CVE-2015-7313
# and 4.1.0 doesn't have the issue
CVE_CHECK_WHITELIST += "CVE-2015-7313"

inherit autotools multilib_header

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

do_install_append() {
    oe_multilib_header tiffconf.h
}

BBCLASSEXTEND = "native"
