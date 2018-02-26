SUMMARY = "Provides support for the Tag Image File Format (TIFF)"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=34da3db46fab7501992f9615d7e158cf"

CVE_PRODUCT = "libtiff"

SRC_URI = "http://download.osgeo.org/libtiff/tiff-${PV}.tar.gz \
           file://libtool2.patch \
           file://CVE-2017-9147.patch \
           file://CVE-2017-9936.patch \
           file://CVE-2017-10688.patch \
           file://CVE-2017-11335.patch \
           file://CVE-2017-13726.patch \
           file://CVE-2017-13727.patch \
          "

SRC_URI[md5sum] = "2a7d1c1318416ddf36d5f6fa4600069b"
SRC_URI[sha256sum] = "59d7a5a8ccd92059913f246877db95a2918e6c04fb9d43fd74e5c3390dac2910"

# exclude betas
UPSTREAM_CHECK_REGEX = "tiff-(?P<pver>\d+(\.\d+)+).tar"

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
