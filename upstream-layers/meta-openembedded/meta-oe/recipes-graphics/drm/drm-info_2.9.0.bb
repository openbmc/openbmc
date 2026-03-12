SUMMARY = "Small utility to dump info about DRM devices"
HOMEPAGE = "https://gitlab.freedesktop.org/emersion/drm_info"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32fd56d355bd6a61017655d8da26b67c"

SRC_URI = "git://gitlab.freedesktop.org/emersion/drm_info.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "67a49ad122e8fa5c85430a8e22f20b7f56e750d5"


inherit meson pkgconfig

DEPENDS = "json-c libdrm"
