require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://0002-do-not-display-CLFAGS-to-improve-reproducibility-bui.patch \
           file://0001-remap-arm-Adjust-inline-asm-constraints.patch \
           file://volatiles.04_pulse \
           "
SRC_URI[md5sum] = "e41d606f90254ed45c90520faf83d95c"
SRC_URI[sha256sum] = "961b23ca1acfd28f2bc87414c27bb40e12436efcf2158d29721b1e89f3f28057"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
