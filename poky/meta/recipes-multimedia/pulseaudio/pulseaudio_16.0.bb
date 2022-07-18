require pulseaudio.inc

SRC_URI = "http://freedesktop.org/software/pulseaudio/releases/${BP}.tar.xz \
           file://0001-client-conf-Add-allow-autospawn-for-root.patch \
           file://0002-do-not-display-CLFAGS-to-improve-reproducibility-bui.patch \
           file://volatiles.04_pulse \
           file://0001-doxygen-meson.build-remove-dependency-on-doxygen-bin.patch \
           "
SRC_URI[sha256sum] = "b4ec6271910a1a86803f165056547f700dfabaf8d5c6c69736f706b5bb889f47"
UPSTREAM_CHECK_REGEX = "pulseaudio-(?P<pver>\d+(\.(?!99)\d+)+)\.tar"
