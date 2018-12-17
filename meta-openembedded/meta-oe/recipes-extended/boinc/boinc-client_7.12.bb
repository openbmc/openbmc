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
SRCREV = "bd12338dbd29083daa5a4b022592ca31ff68cd98"
BRANCH = "client_release/7/${PV}"
SRC_URI = "git://github.com/BOINC/boinc;protocol=https;branch=${BRANCH} \
           file://boinc-AM_CONDITIONAL.patch \
           file://opengl_m4_check.patch \
           file://gtk-configure.patch \
"

inherit gettext autotools pkgconfig distro_features_check systemd

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
do_install_append() {
	if [ -e ${D}${libdir}/systemd/system/boinc-client.service ]; then
		install -D -m 0644 \
		${D}${libdir}/systemd/system/boinc-client.service \
		${D}${systemd_system_unitdir}/boinc-client.service
		rm -rf ${D}${libdir}/systemd
	fi
}

SYSTEMD_SERVICE_${PN} = "boinc-client.service"

FILES_${PN} += "${libdir}/systemd"
