SUMMARY = "FFmpeg nvidia headers"
HOMEPAGE = "https://git.videolan.org/git/ffmpeg/nv-codec-headers.git"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/include/ffnvcodec/dynlink_cuda.h;beginline=1;endline=26;md5=bb54a418154445b0aa99e15f640eacf4"

SRC_URI = "git://git.videolan.org/git/ffmpeg/nv-codec-headers.git;branch=master;protocol=https"
SRCREV = "f8ae7a49bfef2f99d2c931a791dc3863fda67bf3"
S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${includedir}/ffnvcodec ${D}${libdir}/pkgconfig
    for file in include/ffnvcodec/*.h; do
        install -m 644 "$file" ${D}${includedir}/ffnvcodec
    done
    install -m 644 ffnvcodec.pc.in ${D}${libdir}/pkgconfig/ffnvcodec.pc
    sed -i "s|@@PREFIX@@|${prefix}|" ${D}${libdir}/pkgconfig/ffnvcodec.pc
}
