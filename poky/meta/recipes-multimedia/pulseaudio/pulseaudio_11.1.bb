require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-padsp-Make-it-compile-on-musl.patch \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://volatiles.04_pulse \
           file://0001-memfd-wrappers-only-define-memfd_create-if-not-alrea.patch \
           "
SRC_URI[md5sum] = "390de38231d5cdd6b43ada8939eb74f1"
SRC_URI[sha256sum] = "f2521c525a77166189e3cb9169f75c2ee2b82fa3fcf9476024fbc2c3a6c9cd9e"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
