require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://0002-do-not-display-CLFAGS-to-improve-reproducibility-bui.patch \
           file://0001-build-sys-Add-an-option-for-enabling-disabling-Valgr.patch \
           file://0001-meson-Check-for-__get_cpuid.patch \
           file://volatiles.04_pulse \
           "
SRC_URI[md5sum] = "84a7776e63dd55c40db8fbd7c7e2e18e"
SRC_URI[sha256sum] = "a834775d9382b055504e5ee7625dc50768daac29329531deb6597bf05e06c261"
UPSTREAM_CHECK_REGEX = "pulseaudio-(?P<pver>\d+(\.(?!99)\d+)+)\.tar"
