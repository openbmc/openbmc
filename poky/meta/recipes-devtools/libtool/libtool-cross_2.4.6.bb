require libtool-${PV}.inc

PACKAGES = ""
SRC_URI += "file://fixinstall.patch"

datadir = "${STAGING_DIR_TARGET}${target_datadir}"

inherit nopackages

do_configure:prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

#
# ccache may or may not be INHERITED, we remove references to it so the sstate
# artefact works on a machine where its not present. libtool-cross isn't used
# heavily so any performance issue is minor.
# Find references to LTCC="ccache xxx-gcc" and CC="ccache xxx-gcc"
#
do_install () {
	ln -s false ${D}
	install -d ${D}${bindir_crossscripts}/
	install -m 0755 libtool ${D}${bindir_crossscripts}/libtool
	sed -e 's@^\(predep_objects="\).*@\1"@' \
	    -e 's@^\(postdep_objects="\).*@\1"@' \
	    -e 's@^CC="ccache.@CC="@' \
	    -e 's@^LTCC="ccache.@LTCC="@' \
	    -i ${D}${bindir_crossscripts}/libtool
	sed -i '/^archive_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/libtool
	sed -i '/^archive_expsym_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/libtool
	GREP='/bin/grep' SED='sed' ${S}/build-aux/inline-source libtoolize > ${D}${bindir_crossscripts}/libtoolize
	chmod 0755 ${D}${bindir_crossscripts}/libtoolize
	install -d ${D}${target_datadir}/libtool/build-aux/
	install -d ${D}${target_datadir}/aclocal/
	install -c ${S}/build-aux/compile ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.guess ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.sub ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/depcomp ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/install-sh ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/missing ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/build-aux/ltmain.sh ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/m4/*.m4 ${D}${target_datadir}/aclocal/
}

SYSROOT_DIRS += "${bindir_crossscripts} ${target_datadir}"

SSTATE_SCAN_FILES += "libtoolize *-libtool"
