require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-padsp-Make-it-compile-on-musl.patch \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://0001-alsa-bluetooth-fail-if-user-requested-profile-doesn-.patch \
           file://0002-card-don-t-allow-the-CARD_NEW-hook-to-fail.patch \
           file://0003-card-move-profile-selection-after-pa_card_new.patch \
           file://0004-card-remove-pa_card_new_data.active_profile.patch \
           file://0005-alsa-set-availability-for-some-unavailable-profiles.patch \
           file://volatiles.04_pulse \
"
SRC_URI[md5sum] = "da7162541b3a9bc20576dbd0d7d1489a"
SRC_URI[sha256sum] = "c3d3d66b827f18fbe903fe3df647013f09fc1e2191c035be1ee2d82a9e404686"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
