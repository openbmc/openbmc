SUMMARY = "libheif is an ISO/IEC 23008-12:2017 HEIF and AVIF (AV1 Image File Format) file format decoder and encoder"
HOMEPAGE = "https://github.com/strukturag/libheif"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c0edc7369719b2c47d44e80ba74b4b7"
LICENSE_FLAGS = "commercial"

COMPATIBLE_MACHINE:powerpc64le = "null"

SRC_URI = "git://github.com/strukturag/libheif.git;protocol=https;branch=master"

SRCREV = "5e9deb19fe6b3768af0bb8e9e5e8438b15171bf3"

inherit cmake pkgconfig

DEPENDS += "gdk-pixbuf x265 libde265 tiff zlib libpng libwebp"
PACKAGECONFIG += "dav1d jpeg jpeg2000 ffmpeg aom openh264"
PACKAGECONFIG:remove:riscv32 = "openh264"
PACKAGECONFIG[aom] = "-DWITH_AOM_ENCODER=ON -DWITH_AOM_DECODER=ON,-DWITH_AOM_ENCODER=OFF -DWITH_AOM_DECODER=OFF,aom"
PACKAGECONFIG[dav1d] = "-DWITH_DAV1D=ON, -DWITH_DAV1D=OFF,dav1d"
PACKAGECONFIG[svt-av1] = "-DWITH_SvtEnc=ON,-DWITH_SvtEnc=OFF,svt-av1"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG_DECODER=ON -DWITH_JPEG_ENCODER=ON,-DWITH_JPEG_ENCODER=OFF -DWITH_JPEG_DECODER=OFF,jpeg"
PACKAGECONFIG[ffmpeg] = "-DWITH_FFMPEG_DECODER=ON,-DWITH_FFMPEG_DECODER=OFF,ffmpeg"
PACKAGECONFIG[jpeg2000] = "-DWITH_OpenJPEG_ENCODER=ON -DWITH_OpenJPEG_DECODER=ON,-DWITH_OpenJPEG_ENCODER=OFF -DWITH_OpenJPEG_DECODER=OFF,openjpeg"
PACKAGECONFIG[openh264] = ",,openh264"

FILES:${PN} += "${libdir}/libheif ${datadir}/thumbnailers ${libdir}/gdk-pixbuf*"
