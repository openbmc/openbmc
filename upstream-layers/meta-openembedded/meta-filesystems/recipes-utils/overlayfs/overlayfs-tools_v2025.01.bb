DESCRIPTION = "Maintenance tools for OverlayFS"
HOMEPAGE = "https://github.com/kmxz/overlayfs-tools"
LICENSE = "WTFPL"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f312a7c4d02230e8f2b537295d375c69"

SRC_URI = "\
    git://github.com/kmxz/overlayfs-tools.git;protocol=https;branch=master;tag=${PV} \
"

SRCREV = "6e925bbbe747fbb58bc4a95a646907a2101741f6"

inherit meson pkgconfig

DEPENDS:append:libc-musl = " fts"
