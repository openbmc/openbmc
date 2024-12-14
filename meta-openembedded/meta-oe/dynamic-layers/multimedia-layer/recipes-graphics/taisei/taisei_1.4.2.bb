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
    libsdl2 \
    libwebp \
    opusfile \
    zstd \
"
DEPENDS:append:libc-musl = " libucontext"
LDFLAGS:append:libc-musl = " -lucontext"

RDEPENDS_${PN} = "\
    cglm (>= 0.7.8) \
    freetype \
    libsdl2 (>= 2.0.16) \
    libpng (>= 1.5.0) \
    libwebp (>= 0.5) \
    libzstd (>= 1.4.0) \
    opengl (>= 3.3) \
    opusfile \
    zlib \
"

SRC_URI = "gitsm://github.com/taisei-project/taisei.git;branch=v1.4.x;protocol=https \
           file://0001-util-consideredharmful-Use-overloadable-func-attribu.patch \
           file://0001-Remove-strip-option-from-executable-build.patch"

SRCREV = "c098579d4fa0f004ccc204c5bc46eac3717cba28"

S = "${WORKDIR}/git"

inherit features_check meson mime mime-xdg pkgconfig python3native

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= ""

PACKAGECONFIG[a_null] = "-Da_null=true,-Da_null=false"
PACKAGECONFIG[developer] = "-Ddeveloper=true,-Ddeveloper=false"
PACKAGECONFIG[docs] = "-Ddocs=true,-Ddocs=false,python3-docutils-native"

FILES:${PN} += "${datadir}"
