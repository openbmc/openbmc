SUMMARY = "A parallel build system"
DESCRIPTION = "distcc is a parallel build system that distributes \
compilation of C/C++/ObjC code across machines on a network."
HOMEPAGE = "https://github.com/distcc/distcc"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "avahi binutils"

PACKAGECONFIG ??= "popt"
PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk --without-gnome,gtk+"
# use system popt by default
PACKAGECONFIG[popt] = "--without-included-popt,--with-included-popt,popt"

RRECOMMENDS_${PN}-server = "avahi-daemon"

SRC_URI = "git://github.com/distcc/distcc.git \
           file://fix-gnome.patch \
           file://separatebuilddir.patch \
           file://default \
           file://distcc \
           file://distcc.service"
SRCREV = "4cde9bcfbda589abd842e3bbc652ce369085eaae"
S = "${WORKDIR}/git"

inherit autotools pkgconfig update-rc.d useradd systemd

ASNEEDED = ""

EXTRA_OECONF += "--disable-Werror PYTHON='' --disable-pump-mode"

PACKAGE_BEFORE_PN = "${PN}-distmon-gnome ${PN}-server"

USERADD_PACKAGES = "${PN}-server"
USERADD_PARAM_${PN}-server = "--system \
                       --home /dev/null \
                       --no-create-home \
                       --gid nogroup \
                       distcc"

UPDATERCPN = "${PN}-server"
INITSCRIPT_NAME = "distcc"

SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE_${PN}-server = "distcc.service"

do_install() {
    # Improve reproducibility: compress w/o timestamps
    oe_runmake 'DESTDIR=${D}'  "GZIP_BIN=gzip -n" install
    install -d ${D}${sysconfdir}/init.d/
    install -d ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/distcc ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/default ${D}${sysconfdir}/default/distcc
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/distcc.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BINDIR@,${bindir},g' ${D}${systemd_unitdir}/system/distcc.service
}

FILES_${PN}-server = "${sysconfdir} \
                      ${bindir}/distccd \
                      ${sbindir}"
FILES_${PN}-distmon-gnome = "${bindir}/distccmon-gnome \
                             ${datadir}/applications \
                             ${datadir}/pixmaps"

#
# distcc upstream dropped the 3.2 branch which we reference in older project releases
# the revisions are there, just the branch is not. In order to be able to continue
# to build those old releases, adjust any mirror tarball to contain the missing branch
#
fixup_distcc_mirror_tarball () {
	TBALL=${DL_DIR}/git2_github.com.distcc.distcc.git.tar.gz
	if [ -f $TBALL ]; then
		TDIR=`mktemp -d`
		cd $TDIR
		tar -xzf $TBALL
		set +e
		git rev-parse --verify 3.2
		if [ "$?" != "0" ]; then
			git branch 3.2 d8b18df3e9dcbe4f092bed565835d3975e99432c
			tar -czf $TBALL *
		fi
		set -e
		rm -rf $TDIR/*
	fi
}
do_fetch[postfuncs] += "fixup_distcc_mirror_tarball"
