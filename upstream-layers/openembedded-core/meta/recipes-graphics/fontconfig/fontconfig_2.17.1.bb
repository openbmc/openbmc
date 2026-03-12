SUMMARY = "Generic font configuration library"
DESCRIPTION = "Fontconfig is a font configuration and customization library, which \
does not depend on the X Window System. It is designed to locate \
fonts within the system and select them according to requirements \
specified by applications. \
Fontconfig is not a rasterization library, nor does it impose a \
particular rasterization library on the application. The X-specific \
library 'Xft' uses fontconfig along with freetype to specify and \
rasterize fonts."

HOMEPAGE = "http://www.fontconfig.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=fontconfig"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=00252fd272bf2e722925613ad74cb6c7 \
                    file://src/fcfreetype.c;endline=23;md5=f7c0140c1b0387cf4cf45420b059847c \
                    "

SECTION = "libs"

DEPENDS = "expat freetype zlib gperf-native util-linux"

SRC_URI = "https://gitlab.freedesktop.org/api/v4/projects/890/packages/generic/fontconfig/${PV}/fontconfig-${PV}.tar.xz \
           file://revert-static-pkgconfig.patch \
           file://musl-fix.patch \
           file://0001-build-Added-missing-target-rule-dependencies.patch \
           "
SRC_URI[sha256sum] = "9f5cae93f4fffc1fbc05ae99cdfc708cd60dfd6612ffc0512827025c026fa541"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/fontconfig/fontconfig/-/tags"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>.+)"

do_install:append:class-target() {
    # duplicate fc-cache for postinstall script
    mkdir -p ${D}${libexecdir}
    ln ${D}${bindir}/fc-cache ${D}${libexecdir}/${MLPREFIX}fc-cache
}

do_install:append:class-nativesdk() {
    # duplicate fc-cache for postinstall script
    mkdir -p ${D}${libexecdir}
    ln ${D}${bindir}/fc-cache ${D}${libexecdir}/${MLPREFIX}fc-cache
}

PACKAGES =+ "fontconfig-utils"
FILES:${PN} =+ "${datadir}/xml/*"
FILES:${PN}-dev += "${datadir}/gettext/*"
FILES:fontconfig-utils = "${bindir}/* ${libexecdir}/*"

# Work around past breakage in debian.bbclass
RPROVIDES:fontconfig-utils = "libfontconfig-utils"
RREPLACES:fontconfig-utils = "libfontconfig-utils"
RCONFLICTS:fontconfig-utils = "libfontconfig-utils"
DEBIAN_NOAUTONAME:fontconfig-utils = "1"

inherit meson pkgconfig gettext

FONTCONFIG_CACHE_DIR ?= "${localstatedir}/cache/fontconfig"

# comma separated list of additional directories
# /usr/share/fonts is already included by default (you can change it with --with-default-fonts)
FONTCONFIG_FONT_DIRS ?= "no"

EXTRA_OEMESON = "\
    -Dadditional-fonts-dirs=${FONTCONFIG_FONT_DIRS} \
    -Dcache-dir=${FONTCONFIG_CACHE_DIR} \
    -Ddefault-fonts-dirs=${datadir}/fonts \
    -Ddoc=disabled \
    -Dtests=disabled \
"

BBCLASSEXTEND = "native nativesdk"
