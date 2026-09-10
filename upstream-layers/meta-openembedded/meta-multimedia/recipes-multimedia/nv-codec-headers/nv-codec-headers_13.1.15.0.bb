SUMMARY = "FFmpeg nvidia headers"
HOMEPAGE = "https://git.videolan.org/git/ffmpeg/nv-codec-headers.git"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/include/ffnvcodec/dynlink_cuda.h;beginline=1;endline=26;md5=bb54a418154445b0aa99e15f640eacf4"

SRC_URI = " \
    git://git.videolan.org/git/ffmpeg/nv-codec-headers.git;branch=master;protocol=https \
    file://0001-Makefile-add-clean-target.patch \
"
SRCREV = "0a6fba9a2820628b8103464f4c8753ee05838baa"

EXTRA_OEMAKE = "PREFIX=${prefix} DESTDIR=${D} LIBDIR=${baselib}"

do_install() {
    oe_runmake install
}
