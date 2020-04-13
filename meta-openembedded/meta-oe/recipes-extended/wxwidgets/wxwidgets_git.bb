SUMMARY = "Cross-Plattform GUI Library"
DESCRIPTIOM = "wxWidgets is a free and open source cross-platform C++ framework for writing advanced GUI applications using native controls."
HOMEPAGE = "https://www.wxwidgets.org/"
BUGTRACKER = "https://trac.wxwidgets.org/"

# wxWidgets licence is a modified version of LGPL explicitly allowing not
# distributing the sources of an application using the library even in the
# case of static linking.
LICENSE = "wxWidgets"
LIC_FILES_CHKSUM = "file://docs/licence.txt;md5=981f50a934828620b08f44d75db557c6"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'qt', 'cmake_qt5', 'cmake', d)}
inherit features_check lib_package

# All toolkit-configs except 'no_gui' require x11 explicitly (see toolkit.cmake)
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'no_gui', '', 'x11', d)}"

DEPENDS += " \
    jpeg \
    libpng \
    tiff \
"

SRC_URI = "git://github.com/wxWidgets/wxWidgets.git"
PV = "3.1.3"
SRCREV= "8a40d23b27ed1c80b5a2ca9f7e8461df4fbc1a31"
S = "${WORKDIR}/git"

# These can be either 'builtin' or 'sys' and builtin means cloned soures are
# build. So these cannot be PACKAGECONFIGs and let's use libs where we can (see
# DEPENDS)
EXTRA_OECMAKE += " \
    -DwxUSE_LIBJPEG=sys \
    -DwxUSE_LIBPNG=sys \
    -DwxUSE_LIBTIFF=sys \
    -DwxUSE_REGEX=builtin \
"
EXTRA_OECMAKE_append_libc-musl = " \
    -DHAVE_LOCALE_T=OFF \
"

PACKAGECONFIG ?= "gtk"

# Note on toolkit-PACKAGECONFIGs: select exactly one of 'no_gui' / 'gtk' / 'qt'
PACKAGECONFIG[no_gui] = "-DwxUSE_GUI=OFF,,,,,qt gtk"
PACKAGECONFIG[gtk] = "-DwxBUILD_TOOLKIT=gtk3 -DwxUSE_GUI=ON,,gtk+3,,,no_gui qt"
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
PACKAGECONFIG[sdl_audio] = "-DwxUSE_LIBSDL=ON,-DwxUSE_LIBSDL=OFF,libsdl2"
PACKAGECONFIG[webkit] = "-DwxUSE_WEBVIEW_WEBKIT=ON,-DwxUSE_WEBVIEW_WEBKIT=OFF,webkitgtk,,,no_gui"

do_install_append() {
    # do not ship bindir if empty
    rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

# lib names are not canonical
FILES_SOLIBSDEV = ""

FILES_${PN} += " \
    ${libdir}/libwx_*.so \
    ${libdir}/wx/ \
"

FILES_${PN}-dev += "${libdir}/wx/include/"
