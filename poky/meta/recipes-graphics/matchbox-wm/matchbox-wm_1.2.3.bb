SUMMARY = "Matchbox lightweight window manager"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/wm.h;endline=21;md5=ce20617ac10f26045cc57b8d977ab552 \
                    file://src/main.c;endline=21;md5=508f280276140250ce483e0a44f7a9ec \
                    file://src/wm.c;endline=21;md5=f54584fb0d48cfc2e6876e0f0e272e6c"

SECTION = "x11/wm"
DEPENDS = "libmatchbox virtual/libx11 libxext libxrender startup-notification expat gconf libxcursor libxfixes"

SRCREV = "ce8c1053270d960a7235ab5c3435f707541810a4"
SRC_URI = "git://git.yoctoproject.org/matchbox-window-manager;branch=master;protocol=https \
           file://kbdconfig"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

FILES:${PN} = "${bindir}/* \
               ${datadir}/matchbox \
               ${sysconfdir}/matchbox \
               ${datadir}/themes/blondie/matchbox \
               ${datadir}/themes/Default/matchbox \
               ${datadir}/themes/MBOpus/matchbox"

EXTRA_OECONF = " --enable-startup-notification \
                 --disable-xrm \
                 --enable-expat \
                 --with-expat-lib=${STAGING_LIBDIR} \
                 --with-expat-includes=${STAGING_INCDIR}"

do_install:prepend() {
	install ${UNPACKDIR}/kbdconfig ${S}/data/kbdconfig
}
