SUMMARY = "Epeg is a small library for handling thumbnails"
DESCRIPTION = "Insanely fast JPEG/ JPG thumbnail scaling with the minimum fuss and CPU overhead. It makes use of libjpeg features of being able to load an image by only decoding the DCT coefficients needed to reconstruct an image of the size desired."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e7732a9290ea1e4b034fdc15cf49968d \
                    file://COPYING-PLAIN;md5=f59cacc08235a546b0c34a5422133035"
DEPENDS = "jpeg libexif"

SRC_URI = "git://github.com/mattes/epeg.git"
SRCREV = "d78becc558a682f1be0a78b1af90d1b4a08e5b4e"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

