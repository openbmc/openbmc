SUMMARY = "Provides support for the Tag Image File Format (TIFF)"
DESCRIPTION = "Library provides support for the Tag Image File Format \
(TIFF), a widely used format for storing image data.  This library \
provide means to easily access and create TIFF image files."
HOMEPAGE = "http://www.libtiff.org/"
LICENSE = "libtiff"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a3e32d664d6db1386b4689c8121531c3"

CVE_PRODUCT = "libtiff"

SRC_URI = "http://download.osgeo.org/libtiff/tiff-${PV}.tar.gz \
           file://CVE-2023-6277-At-image-reading-compare-data-size-of-some-tags-data.patch \
           file://CVE-2023-6277-At-image-reading-compare-data-size-of-some-tags-data-2.patch \
           file://CVE-2023-6277-Apply-1-suggestion-s-to-1-file-s.patch \
           file://CVE-2023-6228.patch \
           file://CVE-2023-52355-0001.patch \
           file://CVE-2023-52355-0002.patch \
           file://CVE-2023-52356.patch \
           file://CVE-2024-7006.patch \
           file://CVE-2025-9900.patch \
           "

SRC_URI[sha256sum] = "88b3979e6d5c7e32b50d7ec72fb15af724f6ab2cbf7e10880c360a77e4b5d99a"

# exclude betas
UPSTREAM_CHECK_REGEX = "tiff-(?P<pver>\d+(\.\d+)+).tar"

CVE_STATUS[CVE-2015-7313] = "fixed-version: Tested with check from https://security-tracker.debian.org/tracker/CVE-2015-7313 and already 4.3.0 doesn't have the issue"
CVE_STATUS[CVE-2023-3164] = "cpe-incorrect: Issue only affects the tiffcrop tool not compiled by default since 4.6.0"

CVE_STATUS_GROUPS += "CVE_STATUS_REMOVED_TOOLS"
CVE_STATUS_REMOVED_TOOLS = "CVE-2024-13978 CVE-2025-8176 CVE-2025-8177 CVE-2025-8534 CVE-2025-8851 CVE-2025-8961"
CVE_STATUS_REMOVED_TOOLS[status] = "cpe-incorrect: tools affected by these CVEs are not present in this release"

inherit autotools multilib_header

CACHED_CONFIGUREVARS = "ax_cv_check_gl_libgl=no"

PACKAGECONFIG ?= "cxx jpeg zlib lzma \
                  strip-chopping extrasample-as-alpha check-ycbcr-subsampling"

PACKAGECONFIG[cxx] = "--enable-cxx,--disable-cxx,,"
PACKAGECONFIG[jbig] = "--enable-jbig,--disable-jbig,jbig,"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg,"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib,"
PACKAGECONFIG[lzma] = "--enable-lzma,--disable-lzma,xz,"
PACKAGECONFIG[webp] = "--enable-webp,--disable-webp,libwebp,"
PACKAGECONFIG[zstd] = "--enable-zstd,--disable-zstd,zstd,"
PACKAGECONFIG[libdeflate] = "--enable-libdeflate,--disable-libdeflate,libdeflate,"

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
FILES:tiffxx = "${libdir}/libtiffxx.so.*"
FILES:tiff-utils = "${bindir}/*"

do_install:append() {
    oe_multilib_header tiffconf.h
}

BBCLASSEXTEND = "native nativesdk"
