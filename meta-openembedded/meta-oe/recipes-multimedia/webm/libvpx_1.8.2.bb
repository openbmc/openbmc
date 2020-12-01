SUMMARY = "VPX multi-format codec"
DESCRIPTION = "The BSD-licensed libvpx reference implementation provides en- and decoders for VP8 and VP9 bitstreams."
HOMEPAGE = "http://www.webmproject.org/code/"
BUGTRACKER = "http://code.google.com/p/webm/issues/list"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d5b04755015be901744a78cc30d390d4"

SRCREV = "7ec7a33a081aeeb53fed1a8d87e4cbd189152527"
SRC_URI += "git://chromium.googlesource.com/webm/libvpx;protocol=https \
            file://libvpx-configure-support-blank-prefix.patch \
           "

S = "${WORKDIR}/git"

# ffmpeg links with this and fails
# sysroots/armv4t-oe-linux-gnueabi/usr/lib/libvpx.a(vpx_encoder.c.o)(.text+0xc4): unresolvable R_ARM_THM_CALL relocation against symbol `memcpy@@GLIBC_2.4'
ARM_INSTRUCTION_SET = "arm"

CFLAGS += "-fPIC"

export CC
export LD = "${CC}"

VPXTARGET_armv5te = "armv5te-linux-gcc"
VPXTARGET_armv6 = "armv6-linux-gcc"
VPXTARGET_armv7a = "armv7-linux-gcc"
VPXTARGET ?= "generic-gnu"

CONFIGUREOPTS = " \
    --target=${VPXTARGET} \
    --enable-vp9 \
    --enable-libs \
    --disable-install-docs \
    --disable-static \
    --enable-shared \
    --prefix=${prefix} \
    --libdir=${libdir} \
    --size-limit=16384x16384 \
"

do_configure() {
    ${S}/configure ${CONFIGUREOPTS}
}

do_install() {
    oe_runmake install DESTDIR=${D}
    chown -R root:root ${D}
}
