SECTION = "x11/utils"
SUMMARY = "rxvt terminal clone supporting unicode"
DESCRIPTION = "rxvt-unicode is a clone of the well known \
terminal emulator rxvt, modified to store text in Unicode \
(either UCS-2 or UCS-4) and to use locale-correct input and \
output. It also supports mixing multiple fonts at the \
same time, including Xft fonts."
HOMEPAGE = "https://rxvt.org/"
DEPENDS = "virtual/libx11 libxt libxft gdk-pixbuf libxmu"

SRC_URI = "http://dist.schmorp.de/rxvt-unicode/Attic/rxvt-unicode-${PV}.tar.bz2 \
	   file://xwc.patch \
	   file://rxvt.desktop \
	   file://rxvt.png"

inherit autotools pkgconfig update-alternatives

PROVIDES = "virtual/x-terminal-emulator"
ALTERNATIVE_${PN} = "x-terminal-emulator"
ALTERNATIVE_TARGET[x-terminal-emulator] = "${bindir}/rxvt"

CFLAGS_append = " -fpermissive"

EXTRA_OECONF = "--enable-xim \
		--enable-utmp --enable-wtmp --enable-lastlog \
		--with-term=rxvt --enable-keepscrolling \
		--enable-xft --with-name=rxvt --enable-frills \
		--enable-swapscreen --enable-transparency \
		--with-codesets=eu --enable-pointer-blank \
		--enable-text-blink --enable-rxvt-scroll \
		--enable-combining --disable-perl \
		--with-x=${STAGING_DIR_HOST}${prefix}"

EXTRA_OECONF_append_libc-musl = " --disable-wtmp --disable-lastlog"

PACKAGECONFIG ??= ""
PACKAGECONFIG[startup] = "--enable-startup-notification,--disable-startup-notification,startup-notification,"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cp ${S}/aclocal.m4 ${S}/acinclude.m4
	fi
}

do_compile_prepend () {
	echo '#define UTMP_FILE "${localstatedir}/run/utmp"' >> config.h
	echo '#define WTMP_FILE "${localstatedir}/log/wtmp"' >> config.h
	echo '#define LASTLOG_FILE "${localstatedir}/log/lastlog"' >> config.h
	echo '#define HAVE_XLOCALE 1' >> config.h
}

do_install_append () {
	install -d ${D}/${datadir}
	install -d ${D}/${datadir}/applications
	install -d ${D}/${datadir}/pixmaps/

	install -m 0644 ${WORKDIR}/rxvt.png ${D}/${datadir}/pixmaps
	install -m 0644 ${WORKDIR}/rxvt.desktop ${D}/${datadir}/applications
}

FILES_${PN} += "${datadir}/applications/rxvt.desktop ${datadir}/pixmaps/rxvt.png"
