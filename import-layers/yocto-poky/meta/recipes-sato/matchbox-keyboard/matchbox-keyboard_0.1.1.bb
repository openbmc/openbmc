SUMMARY = "Matchbox virtual keyboard for X11"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"
SECTION = "x11"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://src/matchbox-keyboard.h;endline=17;md5=9d6586c69e4a926f3cb0b4425f24ba3c \
                    file://applet/applet.c;endline=18;md5=4a0f721724746b14d95b51ddd42b95e7"

DEPENDS = "libfakekey expat libxft"

#SRCREV for 0.1.1
SRCREV = "630d89068dc0a1e9199306d405cb32f892dfa4d3"
SRC_URI = "git://git.yoctoproject.org/${BPN};branch=matchbox-keyboard-0-1 \
           file://0001-desktop-file-Hide-the-keyboard-from-app-list.patch \
           file://80matchboxkeyboard.sh"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext gtk-immodules-cache distro_features_check

# The libxft, libfakekey and matchbox-panel-2 requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--disable-cairo"

PACKAGECONFIG ??= "gtk3-im applet"
PACKAGECONFIG[applet] = "--enable-applet,--disable-applet,matchbox-panel-2"
PACKAGECONFIG[gtk2-im] = "--enable-gtk-im,--disable-gtk-im,gtk+"
PACKAGECONFIG[gtk3-im] = "--enable-gtk3-im,--disable-gtk3-im,gtk+3"

PACKAGES += "${PN}-im ${PN}-applet"

FILES_${PN} = "${bindir}/ \
	       ${sysconfdir} \
	       ${datadir}/applications \
	       ${datadir}/pixmaps \
	       ${datadir}/matchbox-keyboard"

FILES_${PN}-im = "${libdir}/gtk-2.0/*/immodules/*.so \
                  ${libdir}/gtk-3.0/*/immodules/*.so"

FILES_${PN}-applet = "${libdir}/matchbox-panel/*.so"


do_install_append () {
	install -d ${D}/${sysconfdir}/X11/Xsession.d/
	install -m 755 ${WORKDIR}/80matchboxkeyboard.sh ${D}/${sysconfdir}/X11/Xsession.d/

	rm -f ${D}${libdir}/gtk-2.0/*/immodules/*.la
	rm -f ${D}${libdir}/gtk-3.0/*/immodules/*.la
	rm -f ${D}${libdir}/matchbox-panel/*.la
}

GTKIMMODULES_PACKAGES = "${PN}-im"

RDEPENDS_${PN} = "formfactor dbus-wait"
RRECOMMENDS_${PN} = "${PN}-applet"
