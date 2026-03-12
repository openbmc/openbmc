SUMMARY = "A suite of libcamera-based apps"
DESCRIPTION = "This is a small suite of libcamera-based apps that aim to \
copy the functionality of the existing \"raspicam\" apps."
HOMEPAGE = "https://github.com/raspberrypi/rpicam-apps"
SECTION = "console/utils"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=a0013d1b383d72ba4bdc5b750e7d1d77"

SRC_URI = "\
    git://github.com/raspberrypi/rpicam-apps.git;protocol=https;branch=main \
    file://0001-utils-version.py-use-usr-bin-env-in-shebang.patch \
    file://add-missing-header.patch \
"

SRCREV = "eca9928b76c106141667288f1cef935d02ba59b3"

PROVIDES += "rpicam-apps"

DEPENDS = "libcamera libexif jpeg tiff libpng boost"

PACKAGECONFIG ??= "drm"
PACKAGECONFIG[libav] = "-Denable_libav=enabled, -Denable_libav=disabled, libav"
PACKAGECONFIG[drm] = "-Denable_drm=enabled, -Denable_drm=disabled, libdrm"
PACKAGECONFIG[egl] = "-Denable_egl=enabled, -Denable_egl=disabled, virtual/egl"
PACKAGECONFIG[qt] = "-Denable_qt=enabled, -Denable_qt=disabled, qtbase"
PACKAGECONFIG[opencv] = "-Denable_opencv=enabled, -Denable_opencv=disabled, opencv"
PACKAGECONFIG[tflite] = "-Denable_tflite=enabled, -Denable_tflite=disabled, tensorflow-lite"

inherit meson pkgconfig

NEON_FLAGS = ""
NEON_FLAGS:aarch64 = "-Dneon_flags=arm64"
NEON_FLAGS:arm:raspberrypi3 = "-Dneon_flags=armv8-neon"
NEON_FLAGS:arm:raspberrypi4 = "-Dneon_flags=armv8-neon"
EXTRA_OEMESON += "${NEON_FLAGS}"

# QA Issue: /usr/bin/camera-bug-report contained in package libcamera-apps requires /usr/bin/python3
# QA Issue: File /usr/lib/pkgconfig/rpicam_app.pc in package libcamera-apps-dev contains reference to TMPDIR
do_install:append() {
    rm -v ${D}/${bindir}/camera-bug-report
    sed -i "s,${RECIPE_SYSROOT}${libdir},$\{libdir},g" ${D}${libdir}/pkgconfig/rpicam_app.pc
}

FILES:${PN} += "${libdir}/rpicam-apps-postproc \
                ${libdir}/rpicam-apps-preview \
                ${datadir}/rpi-camera-assets"
