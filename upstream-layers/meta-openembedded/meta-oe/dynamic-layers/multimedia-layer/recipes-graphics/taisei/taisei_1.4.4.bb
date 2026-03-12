SUMMARY = "Taisei Project is an open source danmaku"
DESCRIPTION = "Taisei Project is an open source fan-game set in the world of \
               Tōhō Project. It is a top-down vertical-scrolling curtain fire \
               shooting game (STG), also known as a 'bullet hell' or 'danmaku'. \
               STGs are fast-paced games focused around pattern recognition and \
               mastery through practice."
HOMEPAGE = "https://taisei-project.org"
BUGTRACKER = "https://github.com/taisei-project/taisei/issues"
LICENSE = "CC-BY-4.0 & CC0-1.0 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1a11ffd7e1bdd1d3156cecec60a2846f"

DEPENDS = "\
    cglm \
    freetype \
    libsdl3 \
    libwebp \
    opusfile \
    zstd \
"
DEPENDS:append:libc-musl = " libucontext"
LDFLAGS:append:libc-musl = " -lucontext"

SRC_URI = "gitsm://github.com/taisei-project/taisei.git;branch=v1.4.x;protocol=https;tag=v${PV}"
SRCREV = "6a484e6e61cc51a22a9943762dc2ff6883914f38"

inherit features_check meson mime mime-xdg pkgconfig

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'lto', d)}"

PACKAGECONFIG[a_null] = "-Da_null=true,-Da_null=false"
PACKAGECONFIG[developer] = "-Ddeveloper=true,-Ddeveloper=false"
PACKAGECONFIG[docs] = "-Ddocs=true,-Ddocs=false,python3-docutils-native"
PACKAGECONFIG[rtti] = "-Dcpp_rtti=true,-Dcpp_rtti=false,"
PACKAGECONFIG[lto] = "-Db_lto=true,-Db_lto=false,"

EXTRA_OEMESON += "-Dstrip=false"

FILES:${PN} += "${datadir}"
