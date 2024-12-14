SUMMARY = "Small utility to dump info about DRM devices"
HOMEPAGE = "https://gitlab.freedesktop.org/emersion/drm_info"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32fd56d355bd6a61017655d8da26b67c"

SRC_URI = "git://gitlab.freedesktop.org/emersion/drm_info.git;branch=master;protocol=https"
SRCREV = "c1f5ca4cf750b26eb26c1d9d5c2ef057acbcfefc"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS = "json-c libdrm"
