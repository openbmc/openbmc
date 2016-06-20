require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-padsp-Make-it-compile-on-musl.patch \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://volatiles.04_pulse \
           file://0001-card-add-pa_card_profile.ports.patch \
           file://0002-alsa-bluetooth-fail-if-user-requested-profile-doesn-.patch \
           file://0003-card-move-profile-selection-after-pa_card_new.patch \
           file://0004-alsa-set-availability-for-some-unavailable-profiles.patch \
           file://0001-Revert-module-switch-on-port-available-Route-to-pref.patch \
"
SRC_URI[md5sum] = "8678442ba0bb4b4c33ac6f62542962df"
SRC_URI[sha256sum] = "690eefe28633466cfd1ab9d85ebfa9376f6b622deec6bfee5091ac9737cd1989"

do_compile_prepend() {
    mkdir -p ${S}/libltdl
    cp ${STAGING_LIBDIR}/libltdl* ${S}/libltdl
}
