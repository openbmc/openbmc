SUMMARY = "A suite of libcamera-based apps"
DESCRIPTION = "This is a small suite of libcamera-based apps that aim to \
copy the functionality of the existing \"raspicam\" apps."
HOMEPAGE = "https://github.com/raspberrypi/libcamera-apps"
SECTION = "console/utils"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=a0013d1b383d72ba4bdc5b750e7d1d77"

SRC_URI = "\
    git://github.com/raspberrypi/libcamera-apps.git;protocol=https;branch=main \
    file://0001-utils-version.py-use-usr-bin-env-in-shebang.patch \
    file://0002-Revert-Support-compressed-pixel-formats-when-saving-.patch \
"
PV = "1.4.2+git${SRCPV}"
SRCREV = "9ae39f85ae6bee9761c36b9b5b80d675bc1fa369"

DEPENDS = "libcamera libexif jpeg tiff libpng boost"

PACKAGECONFIG ??= "drm"
PACKAGECONFIG[libav] = "-Denable_libav=true, -Denable_libav=false, libav"
PACKAGECONFIG[drm] = "-Denable_drm=true, -Denable_drm=false, libdrm"
PACKAGECONFIG[egl] = "-Denable_egl=true, -Denable_egl=false, virtual/egl"
PACKAGECONFIG[qt] = "-Denable_qt=true, -Denable_qt=false, qtbase"
PACKAGECONFIG[opencv] = "-Denable_opencv=true, -Denable_opencv=false, opencv"
PACKAGECONFIG[tflite] = "-Denable_tflite=true, -Denable_tflite=false, tensorflow-lite"

inherit meson pkgconfig

NEON_FLAGS = ""
NEON_FLAGS:aarch64 = "-Dneon_flags=arm64"
NEON_FLAGS:arm:raspberrypi3 = "-Dneon_flags=armv8-neon"
NEON_FLAGS:arm:raspberrypi4 = "-Dneon_flags=armv8-neon"
EXTRA_OEMESON += "${NEON_FLAGS}"

# QA Issue: /usr/bin/camera-bug-report contained in package libcamera-apps requires /usr/bin/python3
do_install:append() {
    rm -v ${D}/${bindir}/camera-bug-report
}

# not picked automatically, because it's missing common 'lib' prefix
FILES:${PN}-dev += "${libdir}/rpicam_app.so"
