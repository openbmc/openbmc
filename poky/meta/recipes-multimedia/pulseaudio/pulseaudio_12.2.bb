require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://volatiles.04_pulse \
           "
SRC_URI[md5sum] = "c42f1f1465e8df9859d023dc184734bf"
SRC_URI[sha256sum] = "809668ffc296043779c984f53461c2b3987a45b7a25eb2f0a1d11d9f23ba4055"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
