SUMMARY = "Libav-based GStreamer 1.x plugin"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
SECTION = "multimedia"

LICENSE = "GPLv2+ & LGPLv2+ & ( (GPLv2+ & LGPLv2.1+) | (GPLv3+ & LGPLv3+) )"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://ext/libav/gstav.h;beginline=1;endline=18;md5=a752c35267d8276fd9ca3db6994fca9c \
                    file://gst-libs/ext/libav/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://gst-libs/ext/libav/COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://gst-libs/ext/libav/COPYING.LGPLv2.1;md5=bd7a443320af8c812e4c18d1b79df004 \
                    file://gst-libs/ext/libav/COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "http://gstreamer.freedesktop.org/src/gst-libav/gst-libav-${PV}.tar.xz \
           file://0001-Disable-yasm-for-libav-when-disable-yasm.patch \
           file://workaround-to-build-gst-libav-for-i586-with-gcc.patch \
           file://mips64_cpu_detection.patch \
           file://0001-configure-check-for-armv7ve-variant.patch \
           file://0001-fix-host-contamination.patch \
           "
SRC_URI[md5sum] = "58342db11dbb201a66a62577dcf7bab5"
SRC_URI[sha256sum] = "dfd78591901df7853eab7e56a86c34a1b03635da0d3d56b89aa577f1897865da"

S = "${WORKDIR}/gst-libav-${PV}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base zlib bzip2 xz"

inherit autotools pkgconfig upstream-version-is-even gtk-doc

# CAUTION: Using the system libav is not recommended. Since the libav API is changing all the time,
# compilation errors (and other, more subtle bugs) can happen. It is usually better to rely on the
# libav copy included in the gst-libav package.
PACKAGECONFIG ??= "orc yasm"

PACKAGECONFIG[gpl] = "--enable-gpl,--disable-gpl,"
PACKAGECONFIG[libav] = "--with-system-libav,,libav"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[yasm] = "--enable-yasm,--disable-yasm,nasm-native"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind"

GSTREAMER_1_0_DEBUG ?= "--disable-debug"

LIBAV_EXTRA_CONFIGURE = "--with-libav-extra-configure"

LIBAV_EXTRA_CONFIGURE_COMMON_ARG = "--target-os=linux \
  --cc='${CC}' --as='${CC}' --ld='${CC}' --nm='${NM}' --ar='${AR}' \
  --ranlib='${RANLIB}' \
  ${GSTREAMER_1_0_DEBUG} \
  --cross-prefix='${HOST_PREFIX}'"

# Disable assembly optimizations for X32, as this libav lacks the support
PACKAGECONFIG_remove_linux-gnux32 = "yasm"
LIBAV_EXTRA_CONFIGURE_COMMON_ARG_append_linux-gnux32 = " --disable-asm"

LIBAV_EXTRA_CONFIGURE_COMMON = \
'${LIBAV_EXTRA_CONFIGURE}="${LIBAV_EXTRA_CONFIGURE_COMMON_ARG}"'

EXTRA_OECONF = "${LIBAV_EXTRA_CONFIGURE_COMMON}"

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-dev += "${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"

# http://errors.yoctoproject.org/Errors/Details/20493/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

# ffmpeg/libav disables PIC on some platforms (e.g. x86-32)
INSANE_SKIP_${PN} = "textrel"
