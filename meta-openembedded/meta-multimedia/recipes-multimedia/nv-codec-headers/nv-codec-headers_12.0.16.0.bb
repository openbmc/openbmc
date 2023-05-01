SUMMARY = "FFmpeg nvidia headers"
HOMEPAGE = "https://git.videolan.org/git/ffmpeg/nv-codec-headers.git"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/include/ffnvcodec/dynlink_cuda.h;beginline=1;endline=26;md5=bb54a418154445b0aa99e15f640eacf4"

SRC_URI = " \
    git://git.videolan.org/git/ffmpeg/nv-codec-headers.git;branch=master;protocol=https \
    file://0001-Makefile-add-clean-target.patch \
"
SRCREV = "c5e4af74850a616c42d39ed45b9b8568b71bf8bf"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "PREFIX=${prefix} DESTDIR=${D}"

do_install() {
    oe_runmake install
}
