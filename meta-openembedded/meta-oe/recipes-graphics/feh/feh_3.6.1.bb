SUMMARY = "X11 image viewer aimed mostly at console users"
AUTHOR = "Tom Gilbert & Daniel Friesel"
HOMEPAGE = "https://feh.finalrewind.org/"
SECTION = "x11/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f91bd06901085c94bdc50649d98c5059"
DEPENDS = "\
    imlib2 \
    virtual/libx11 libxt\
"

SRC_URI = "https://feh.finalrewind.org/feh-${PV}.tar.bz2"
SRC_URI[sha256sum] = "9b1edec52cbae97b17530cb5db10666abfb9983f51a5d820c89added6f7b1ea8"

inherit mime-xdg features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OEMAKE = "curl=0 xinerama=0 PREFIX=/usr"

do_compile () {
     oe_runmake
}

do_install () {
     oe_runmake install app=1 'DESTDIR=${D}' 'ICON_PREFIX=${D}${datadir}/icons'
}

RDEPENDS_${PN} += "imlib2-loaders"

FILES_${PN} += "${datadir}/icons"
