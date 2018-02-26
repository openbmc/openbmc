# Copyright (C) 2016 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Open-source software for volunteer computing"
DESCRIPTION = "The Berkeley Open Infrastructure for Network Computing (BOINC) is an open- \
source software platform which supports distributed computing, primarily in \
the form of volunteer computing and desktop Grid computing.  It is well \
suited for problems which are often described as trivially parallel.  BOINC \
is the underlying software used by projects such as SETI@home, Einstein@Home, \
ClimatePrediciton.net, the World Community Grid, and many other distributed \
computing projects. \
This package installs the BOINC client software, which will allow your \
computer to participate in one or more BOINC projects, using your spare \
computer time to search for cures for diseases, model protein folding, study \
global warming, discover sources of gravitational waves, and many other types \
of scientific and mathematical research."

HOMEPAGE = "http://boinc.berkeley.edu/"
LICENSE = "LGPLv2+ & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
SECTION = "applications"
DEPENDS = "curl \
           jpeg \
           openssl \
           sqlite3 \
           virtual/libgl \
           ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'libnotify', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk+ libnotify xcb-util libxscrnsaver', '', d)} \
           nettle \
"

SRC_URI = "https://github.com/BOINC/boinc/archive/client_release/7.6/${PV}.tar.gz \
           file://boinc-AM_CONDITIONAL.patch \
           file://opengl_m4_check.patch \
           file://cross-compile.patch \
           file://gtk-configure.patch \
"
SRC_URI[md5sum] = "437b4b98e384b4bda4ef7056e68166ac"
SRC_URI[sha256sum] = "c4b1c29b9655013e0ac61dddf47ad7f30f38c46159f02a9d9dc8ab854e99aa6d"

inherit gettext autotools-brokensep pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES += "opengl"

S = "${WORKDIR}/${BPN}_release-7.6-${PV}"

EXTRA_OECONF += "\
    --enable-libraries \
    --enable-unicode \
    --enable-shared \
    --enable-dynamic-client-linkage \
    --enable-client \
    --disable-server \
    --disable-static \
    --disable-manager \
    --with-ssl=${STAGING_EXECPREFIXDIR} \
    --without-wxdir \
    --without-x \
    --with-boinc-platform=${TARGET_SYS} \
"
export PKG_CONFIG = "${STAGING_BINDIR_NATIVE}/pkg-config"

do_configure_prepend () {
	if "${@bb.utils.contains('DEPENDS', 'gtk+', '1', '0', d)}" = "0"
	then
		export GTK2_CFLAGS=""
		export GTK2_LIBS=""
	fi
}

do_compile_prepend () {
	# Disable rpaths
	sed -i -e 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' ${B}/${TARGET_SYS}-libtool
	sed -i -e 's|^sys_lib_dlsearch_path_spec=.*|sys_lib_dlsearch_path_spec=""|g' ${B}/${TARGET_SYS}-libtool
	sed -i -e 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' ${B}/${TARGET_SYS}-libtool
}

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
