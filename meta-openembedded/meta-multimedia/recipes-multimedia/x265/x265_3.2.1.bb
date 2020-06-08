SUMMARY = "H.265/HEVC video encoder"
DESCRIPTION = "A free software library and application for encoding video streams into the H.265/HEVC format."
HOMEPAGE = "http://www.videolan.org/developers/x265.html"

LICENSE = "GPLv2"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://../COPYING;md5=c9e0427bc58f129f99728c62d4ad4091"

DEPENDS = "nasm-native gnutls zlib libpcre"

SRC_URI = "http://ftp.videolan.org/pub/videolan/x265/x265_${PV}.tar.gz"

S = "${WORKDIR}/x265_${PV}/source"

SRC_URI[md5sum] = "94808045a34d88a857e5eaf3f68f4bca"
SRC_URI[sha256sum] = "fb9badcf92364fd3567f8b5aa0e5e952aeea7a39a2b864387cec31e3b58cbbcc"

inherit lib_package pkgconfig cmake

EXTRA_OECMAKE_append_x86 = " -DENABLE_ASSEMBLY=OFF"

AS[unexport] = "1"

COMPATIBLE_HOST = '(x86_64|i.86).*-linux'
