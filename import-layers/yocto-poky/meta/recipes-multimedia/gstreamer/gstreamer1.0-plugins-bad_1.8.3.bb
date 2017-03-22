include gstreamer1.0-plugins-bad.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://gst/tta/crc32.h;beginline=12;endline=29;md5=27db269c575d1e5317fffca2d33b3b50 \
                    file://gst/tta/filters.h;beginline=12;endline=29;md5=8a08270656f2f8ad7bb3655b83138e5a"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-bad/gst-plugins-bad-${PV}.tar.xz \
    file://configure-allow-to-disable-libssh2.patch \
    file://fix-maybe-uninitialized-warnings-when-compiling-with-Os.patch \
    file://avoid-including-sys-poll.h-directly.patch \
    file://ensure-valid-sentinels-for-gst_structure_get-etc.patch \
    file://0001-gstreamer-gl.pc.in-don-t-append-GL_CFLAGS-to-CFLAGS.patch \
    file://0002-glplugin-enable-gldeinterlace-on-OpenGL-ES.patch \
    file://0003-glcolorconvert-implement-multiple-render-targets-for.patch \
    file://0004-glcolorconvert-don-t-use-the-predefined-variable-nam.patch \
    file://0005-glshader-add-glBindFragDataLocation.patch \
    file://0006-glcolorconvert-GLES3-deprecates-texture2D-and-it-doe.patch \
    file://0008-gl-implement-GstGLMemoryEGL.patch \
    file://0009-glimagesink-Downrank-to-marginal.patch \
"
SRC_URI[md5sum] = "955281a43e98c5464563fa049e0a0911"
SRC_URI[sha256sum] = "7899fcb18e6a1af2888b19c90213af018a57d741c6e72ec56b133bc73ec8509b"

S = "${WORKDIR}/gst-plugins-bad-${PV}"
