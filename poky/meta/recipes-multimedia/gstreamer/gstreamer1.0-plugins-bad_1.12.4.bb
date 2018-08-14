require gstreamer1.0-plugins-bad.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 "

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-bad/gst-plugins-bad-${PV}.tar.xz \
    file://configure-allow-to-disable-libssh2.patch \
    file://fix-maybe-uninitialized-warnings-when-compiling-with-Os.patch \
    file://avoid-including-sys-poll.h-directly.patch \
    file://ensure-valid-sentinels-for-gst_structure_get-etc.patch \
    file://0001-gstreamer-gl.pc.in-don-t-append-GL_CFLAGS-to-CFLAGS.patch \
    file://0009-glimagesink-Downrank-to-marginal.patch \
    file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch \
    file://link-with-libvchostif.patch \
    file://0001-vkdisplay-Use-ifdef-for-platform-specific-defines.patch \
    file://0002-vulkan-Use-the-generated-version-of-vkconfig.h.patch \
"
SRC_URI[md5sum] = "64bf16cf15edaab1c0e02c8dee7170d0"
SRC_URI[sha256sum] = "0c7857be16686d5c1ba6e34bd338664d3d4599d32714a8eca5c8a41a101e2d08"

S = "${WORKDIR}/gst-plugins-bad-${PV}"


