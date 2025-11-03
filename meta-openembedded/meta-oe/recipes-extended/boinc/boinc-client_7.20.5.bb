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
LICENSE = "LGPL-2.0-or-later & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
SECTION = "applications"
DEPENDS = "curl \
           jpeg \
           openssl \
           sqlite3 \
           virtual/egl \
           ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'libnotify', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk+3 wxwidgets libnotify xcb-util libxscrnsaver', '', d)} \
           nettle \
"
SRCREV = "4774e1cbe0ad13cb9a6f7fffbb626a417316f61d"
BRANCH = "client_release/7/7.20"
SRC_URI = "git://github.com/BOINC/boinc;protocol=https;branch=${BRANCH} \
           file://boinc-AM_CONDITIONAL.patch \
           file://0001-scripts-Do-not-check-for-files-on-build-host.patch \
           file://0001-Do-not-undefine-_FILE_OFFSET_BITS.patch \
"

inherit gettext autotools pkgconfig features_check systemd

REQUIRED_DISTRO_FEATURES += "opengl"

S = "${WORKDIR}/git"

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
    ac_cv_c_undeclared_builtin_options='none' \
"
export PKG_CONFIG = "${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile:prepend () {
	# Disable rpaths
	sed -i -e 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' ${B}/libtool
	sed -i -e 's|^sys_lib_dlsearch_path_spec=.*|sys_lib_dlsearch_path_spec=""|g' ${B}/libtool
	sed -i -e 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' ${B}/libtool
}

do_install:prepend() {
	# help script install a bit to do right thing for OE
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		mkdir -p ${D}${systemd_system_unitdir}
	else
		mkdir -p ${D}${sysconfdir}/init.d
	fi
	mkdir -p ${D}${sysconfdir}/default
}

do_install:append() {
	sed -i -e 's#${S}##g' ${D}${includedir}/boinc/svn_version.h
}

SYSTEMD_SERVICE:${PN} = "boinc-client.service"

FILES:${PN} += "${libdir}/systemd"
