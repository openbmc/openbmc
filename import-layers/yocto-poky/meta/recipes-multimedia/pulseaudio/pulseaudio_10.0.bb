require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-padsp-Make-it-compile-on-musl.patch \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://pulseaudio-discuss-iochannel-don-t-use-variable-length-array-in-union.patch \
           file://volatiles.04_pulse \
"
SRC_URI[md5sum] = "4950d2799bf55ab91f6b7f990b7f0971"
SRC_URI[sha256sum] = "a3186824de9f0d2095ded5d0d0db0405dc73133983c2fbb37291547e37462f57"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
