SUMMARY = "A suite of libcamera-based apps for the Raspberry Pi"
DESCRIPTION = "This is a small suite of libcamera-based apps that aim to \
copy the functionality of the existing \"raspicam\" apps."
HOMEPAGE = "https://github.com/raspberrypi/libcamera-apps"
SECTION = "console/utils"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=a0013d1b383d72ba4bdc5b750e7d1d77"

SRC_URI = "\
    git://github.com/raspberrypi/libcamera-apps.git;protocol=https;branch=main \
    file://0001-utils-version.py-use-usr-bin-env-in-shebang.patch \
"
PV = "1.1.2+git${SRCPV}"
SRCREV = "12098520a3dec36ba796655baac7efece457f8d8"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "^rpi$"

DEPENDS = "libcamera libexif jpeg tiff libpng boost"

inherit cmake pkgconfig

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DBoost_INCLUDE_DIR=${STAGING_INCDIR} \
    -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR} \
"

LIBCAMERA_ARCH = "${TARGET_ARCH}"
LIBCAMERA_ARCH:aarch64 = "arm64"
LIBCAMERA_ARCH:arm = "armv8-neon"
EXTRA_OECMAKE += "-DENABLE_COMPILE_FLAGS_FOR_TARGET=${LIBCAMERA_ARCH}"

PACKAGECONFIG[x11] = "-DENABLE_X11=1,-DENABLE_X11=0"
PACKAGECONFIG[qt] = "-DENABLE_QT=1,-DENABLE_QT=0"
PACKAGECONFIG[opencv] = "-DENABLE_OPENCV=1,-DENABLE_OPENCV=0"
PACKAGECONFIG[tensorflow-lite] = "-DENABLE_TFLITE=1,-DENABLE_TFLITE=0"

do_install:append() {
    # Requires python3-core which not all systems may have
    rm -v ${D}/${bindir}/camera-bug-report
}
