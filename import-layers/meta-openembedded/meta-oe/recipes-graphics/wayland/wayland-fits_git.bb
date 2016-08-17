SUMMARY = "Wayland-fits, the Wayland Functional Integration Test Suite"
DESCRIPTION = "Wayland-fits is a fully automated functional integration \
test suite. Its main purpose is to test the functionality and integration of \
client-side (i.e. toolkit) and server-side (compositor) implementations of \
the Wayland protocol."
HOMEPAGE = "https://github.com/01org/wayland-fits"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f8d34cadaf891753c0f00c6cd48f08f5 \
                    file://src/extensions/weston/weston-wfits.cpp;endline=21;md5=848c81e55cf3a30a9f6ed75f0dba7a97"

SRC_URI = "git://github.com/01org/wayland-fits.git"
SRCREV = "f108335e374772ae2818a30ae37fe6fcda81980f"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS = "libcheck boost wayland weston"
RDEPENDS_${PN} = "weston"

EXTRA_OECONF += "--enable-shared --disable-static --with-boost-libdir=${STAGING_LIBDIR}"

PACKAGECONFIG ?= "gtk+3"

PACKAGECONFIG[elementary] = "--enable-efl-tests,--disable-efl-tests,elementary"
PACKAGECONFIG[gtk+3] = "--enable-gtk-tests,--disable-gtk-tests,gtk+3"

do_install_append() {
	rm -f ${D}/${libdir}/weston/*.la
}

FILES_${PN} += "${bindir}/wfits ${libdir}/weston/*.so"
FILES_${PN}-dbg += "${bindir}/.debug ${libdir}/weston/.debug ${prefix}/src"
