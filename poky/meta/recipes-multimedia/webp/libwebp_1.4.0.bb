SUMMARY = "WebP is an image format designed for the Web"
DESCRIPTION = "WebP is a method of lossy and lossless compression that can be \
               used on a large variety of photographic, translucent and \
               graphical images found on the web. The degree of lossy \
               compression is adjustable so a user can choose the trade-off \
               between file size and image quality. WebP typically achieves \
               an average of 30% more compression than JPEG and JPEG 2000, \
               without loss of image quality."
HOMEPAGE = "https://developers.google.com/speed/webp/"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e8dee932c26f2dab503abf70c96d8bb \
                    file://PATENTS;md5=c6926d0cb07d296f886ab6e0cc5a85b7"

SRC_URI = "http://downloads.webmproject.org/releases/webp/${BP}.tar.gz"
SRC_URI[sha256sum] = "61f873ec69e3be1b99535634340d5bde750b2e4447caa1db9f61be3fd49ab1e5"

UPSTREAM_CHECK_URI = "http://downloads.webmproject.org/releases/webp/index.html"

EXTRA_OECONF = " \
    --disable-wic \
    --enable-libwebpmux \
    --enable-libwebpdemux \
    --enable-threading \
"

# Do not trust configure to determine if neon is available.
#
EXTRA_OECONF_ARM = " \
    ${@bb.utils.contains("TUNE_FEATURES","neon","--enable-neon","--disable-neon",d)} \
"
EXTRA_OECONF:append:arm = " ${EXTRA_OECONF_ARM}"
EXTRA_OECONF:append:armeb = " ${EXTRA_OECONF_ARM}"

inherit autotools lib_package

PACKAGECONFIG ??= ""

# libwebpdecoder is a subset of libwebp, don't build it unless requested
PACKAGECONFIG[decoder] = "--enable-libwebpdecoder,--disable-libwebpdecoder"

# Apply for examples programs: cwebp and dwebp
PACKAGECONFIG[gif] = "--enable-gif,--disable-gif,giflib"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[png] = "--enable-png,--disable-png,,libpng"
PACKAGECONFIG[tiff] = "--enable-tiff,--disable-tiff,tiff"

# Apply only for example program vwebp
PACKAGECONFIG[gl] = "--enable-gl,--disable-gl,mesa-glut"

PACKAGES =+ "${PN}-gif2webp"

DESCRIPTION:${PN}-gif2webp = "Simple tool to convert animated GIFs to WebP"
FILES:${PN}-gif2webp = "${bindir}/gif2webp"

BBCLASSEXTEND += "native nativesdk"
