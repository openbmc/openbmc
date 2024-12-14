DESCRIPTION = "Maintenance tools for OverlayFS"
HOMEPAGE = "https://github.com/kmxz/overlayfs-tools"
LICENSE = "WTFPL"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f312a7c4d02230e8f2b537295d375c69"

SRC_URI = "\
    git://github.com/kmxz/overlayfs-tools.git;protocol=https;branch=master \
    file://0001-always-use-glibc-basename.patch \
    file://0002-Change-program_name-to-have-const-attribute.patch \
"

SRCREV = "7a4a0c4f2c6c86aa46a40e3468e394fd4a237491"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS:append:libc-musl = " fts"
