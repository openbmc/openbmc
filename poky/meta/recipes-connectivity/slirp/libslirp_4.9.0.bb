SUMMARY = "A general purpose TCP-IP emulator"
DESCRIPTION = "A general purpose TCP-IP emulator used by virtual machine hypervisors to provide virtual networking services."
HOMEPAGE = "https://gitlab.freedesktop.org/slirp/libslirp"
LICENSE = "BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=bca0186b14e6b05e338e729f106db727"

SRC_URI = "git://gitlab.freedesktop.org/slirp/libslirp.git;protocol=https;branch=master"
SRCREV = "c32a8a1ccaae8490142e67e078336a95c5ffc956"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
