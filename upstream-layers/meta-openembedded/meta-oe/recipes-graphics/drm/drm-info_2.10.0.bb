SUMMARY = "Small utility to dump info about DRM devices"
HOMEPAGE = "https://gitlab.freedesktop.org/emersion/drm_info"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32fd56d355bd6a61017655d8da26b67c"

SRC_URI = "git://gitlab.freedesktop.org/emersion/drm_info.git;branch=master;protocol=https;tag=v${PV} \
           file://0001-Reapply-Find-drm_fourcc.h-in-sysroot.patch \
"
SRCREV = "462458e0f292145b2a9d5a8b65c392eaeef7362d"


inherit meson pkgconfig

DEPENDS = "json-c libdrm"
