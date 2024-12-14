SUMMARY = "Cross-Plattform GUI Library"
DESCRIPTION = "wxWidgets is a free and open source cross-platform C++ framework for writing advanced GUI applications using native controls."
HOMEPAGE = "https://www.wxwidgets.org/"
BUGTRACKER = "https://trac.wxwidgets.org/"

# WXwindows licence is a modified version of LGPL explicitly allowing not
# distributing the sources of an application using the library even in the
# case of static linking.
LICENSE = "WXwindows"
LIC_FILES_CHKSUM = "file://docs/licence.txt;md5=981f50a934828620b08f44d75db557c6"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'qt', 'cmake_qt5', 'cmake', d)}
inherit lib_package binconfig pkgconfig

DEPENDS += " \
    jpeg \
    libpng \
    tiff \
"

SRC_URI = "gitsm://github.com/wxWidgets/wxWidgets.git;branch=3.2;protocol=https \
           file://0001-wx-config.in-Disable-cross-magic-it-does-not-work-fo.patch \
           file://0002-fix-libdir-for-multilib.patch \
           file://0003-create-links-with-relative-path.patch \
           file://0004-don-not-append-system-name-to-lib-name.patch \
           file://0005-wx-config-fix-libdir-for-multilib.patch \
           file://0006-Fix-locale-on-musl.patch \
           file://0007-Set-HAVE_LARGEFILE_SUPPORT-to-1-explicitly.patch \
           "
SRCREV= "5ff25322553c1870cf20a2e1ba6f20ed50d9fe9a"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

# These can be either 'builtin' or 'sys' and builtin means cloned soures are
# build. So these cannot be PACKAGECONFIGs and let's use libs where we can (see
# DEPENDS)
EXTRA_OECMAKE += " \
    -DwxUSE_GLCANVAS_EGL=OFF \
    -DwxUSE_LIBJPEG=sys \
    -DwxUSE_LIBPNG=sys \
    -DwxUSE_LIBTIFF=sys \
    -DwxUSE_REGEX=builtin \
"
EXTRA_OECMAKE:append:class-target = ' -DEGREP="/bin/grep -E"'

# OpenGL support currently seems tied to using libglu, which requires x11
PACKAGECONFIG ?= "${@bb.utils.contains_any('DISTRO_FEATURES', 'x11 wayland', 'gtk', 'no_gui', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'opengl', '', d)} \
"

PACKAGECONFIG:remove:class-native = "opengl"

# Note on toolkit-PACKAGECONFIGs: select exactly one of 'no_gui' / 'gtk' / 'qt'
PACKAGECONFIG[no_gui] = "-DwxUSE_GUI=OFF,,,,,qt gtk opengl"
PACKAGECONFIG[gtk] = "-DwxBUILD_TOOLKIT=gtk3 -DwxUSE_GUI=ON -DwxUSE_PRIVATE_FONTS=ON,,gtk+3,,,no_gui qt"
PACKAGECONFIG[qt] = "-DwxBUILD_TOOLKIT=qt  -DwxUSE_GUI=ON,,qtbase,,,no_gui gtk"
python () {
    pkgconfig = d.getVar('PACKAGECONFIG')
    if (not 'no_gui' in pkgconfig) and (not 'gtk' in pkgconfig) and (not 'qt' in pkgconfig):
        bb.error("PACKAGECONFIG must select a toolkit. Add one of no_gui / gtk / qt!")
}

# Notes on other PACKAGECONFIGs:
# * 'no_gui' overrides some configs below so they are marked as conflicting
#   with 'no_gui' to avoid surprises
# * qt+gstreamer is broken due to incorrect references on glib-2.0 -> mark
#   as conflicting
# * wxUSE_LIBGNOMEVFS is for gtk2 (see init.cmake) which we don't support
#   -> no gvfs PACKAGECONFIG
# * libmspack is in meta-security
PACKAGECONFIG[gstreamer] = "-DwxUSE_MEDIACTRL=ON,-DwxUSE_MEDIACTRL=OFF,gstreamer1.0-plugins-base,,,no_gui qt"
PACKAGECONFIG[libsecret] = "-DwxUSE_SECRETSTORE=ON,-DwxUSE_SECRETSTORE=OFF,libsecret,,,no_gui"
PACKAGECONFIG[lzma] = "-DwxUSE_LIBLZMA=ON,-DwxUSE_LIBLZMA=OFF,xz"
PACKAGECONFIG[mspack] = "-DwxUSE_LIBMSPACK=ON,-DwxUSE_LIBMSPACK=OFF,libmspack"
PACKAGECONFIG[opengl] = "-DwxUSE_OPENGL=ON,-DwxUSE_OPENGL=OFF,libglu"
PACKAGECONFIG[sdl_audio] = "-DwxUSE_LIBSDL=ON,-DwxUSE_LIBSDL=OFF,libsdl2"
PACKAGECONFIG[webkit] = "-DwxUSE_WEBVIEW_WEBKIT=ON,-DwxUSE_WEBVIEW_WEBKIT=OFF,webkitgtk3,,,no_gui"
PACKAGECONFIG[curl] = "-DwxUSE_WEBREQUEST_CURL=ON,-DwxUSE_WEBREQUEST_CURL=OFF,curl"

# Support LFS unconditionally
CXXFLAGS += "-D_FILE_OFFSET_BITS=64"

do_compile:append() {
    # if not at re-compile
    if [ -L ${B}/wx-config ]; then
        # ${B}/wx-config is a symlink for build and not needed after compile
        # So for our purposes do:
        # 1. make a file out of wx-config so that binconfig.bbclass detects it
        # 2. make sure we do not move the file used for compiling into sysroot
        cp --remove-destination `readlink ${B}/wx-config | sed 's:inplace-::'` ${B}/wx-config
    fi
    # 3. Set full sysroot paths so sstate can translate them when setting
    #    up wxwidgets's consumer sysroots
    sed -i \
        -e 's,^includedir=.*,includedir="${STAGING_INCDIR}",g' \
        -e 's,^libdir=.*",libdir="${STAGING_LIBDIR}",g' \
        -e 's,^bindir=.*",bindir="${STAGING_BINDIR}",g' \
        ${B}/wx-config
}

do_install:append() {
    # do not ship bindir if empty
    rmdir --ignore-fail-on-non-empty ${D}${bindir}

    # fix host contamination
    sed -i -e "s#${STAGING_DIR_NATIVE}##g" \
           -e "s#${STAGING_DIR_TARGET}##g" \
           ${D}${libdir}/wx/config/*-unicode-3.2 \
           ${D}${libdir}/cmake/wxWidgets/wxWidgetsTargets.cmake
}

FILES:${PN} += " \
    ${libdir}/wx/ \
"

FILES:${PN}-dev += " \
    ${libdir}/wx/include/ \
    ${libdir}/wx/config/ \
"

RDEPENDS:${PN}-dev += "grep"

BBCLASSEXTEND = "native"
