SUMMARY = "bmaptool alternative written in C++"
DESCRIPTION = "bmap-writer is a command-line utility designed to efficiently write disk images \
to storage devices using block mapping (BMAP). It serves as \
a lightweight alternative to the Yocto BMAP tool, \
bmap-writer is C++ based does not require Python and focuses solely on writing an image."
HOMEPAGE = "https://github.com/embetrix/bmap-writer"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e49f4652534af377a713df3d9dec60cb"

SRC_URI = "git://github.com/embetrix/${BPN};branch=master;protocol=https;tag=${PV} \
           file://0001-device-Use-the-C-library-s-ioctl-request-type-for-BL.patch \
           file://0001-Honour-ImageSize-for-the-last-block.patch \
           "
SRCREV = "c7b1bec0f40217d17e315226552e461a4a816abc"

DEPENDS = "libtinyxml2 libarchive"
inherit cmake pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[libkcapi]  = "-DUSE_KERNEL_CRYPTO_API=ON, -DUSE_KERNEL_CRYPTO_API=OFF, libkcapi"

FILES:${PN} = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
