SUMMARY = "AppArmor another MAC control system"
DESCRIPTION = "user-space parser utility for AppArmor \
 This provides the system initialization scripts needed to use the \
 AppArmor Mandatory Access Control system, including the AppArmor Parser \
 which is required to convert AppArmor text profiles into machine-readable \
 policies that are loaded into the kernel for use with the AppArmor Linux \
 Security Module."
HOMEAPAGE = "http://apparmor.net/"
SECTION = "admin"

LICENSE = "GPLv2 & GPLv2+ & BSD-3-Clause & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fd57a4b0bc782d7b80fd431f10bbf9d0"

DEPENDS = "bison-native apr gettext-native coreutils-native"

SRC_URI = " \
	http://archive.ubuntu.com/ubuntu/pool/main/a/${BPN}/${BPN}_${PV}.orig.tar.gz \
	file://disable_perl_h_check.patch \
	file://crosscompile_perl_bindings.patch \
	file://apparmor.rc \
	file://functions \
	file://apparmor \
	file://apparmor.service \
        file://run-ptest \
	"

SRC_URI[md5sum] = "899fd834dc5c8ebf2d52b97e4a174af7"
SRC_URI[sha256sum] = "b1c489ea11e7771b8e6b181532cafbf9ebe6603e3cb00e2558f21b7a5bdd739a"

PARALLEL_MAKE = ""

inherit pkgconfig autotools-brokensep update-rc.d python3native perlnative ptest cpan
inherit ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','', d)}

S = "${WORKDIR}/apparmor-${PV}"

PACKAGECONFIG ?="man python perl"
PACKAGECONFIG[man] = "--enable-man-pages, --disable-man-pages"
PACKAGECONFIG[python] = "--with-python, --without-python, python3 swig-native"
PACKAGECONFIG[perl] = "--with-perl, --without-perl, perl perl-native swig-native"
PACKAGECONFIG[apache2] = ",,apache2,"

PAMLIB="${@bb.utils.contains('DISTRO_FEATURES', 'pam', '1', '0', d)}"
HTTPD="${@bb.utils.contains('PACKAGECONFIG', 'apache2', '1', '0', d)}"


python() {
    if 'apache2' in d.getVar('PACKAGECONFIG').split() and \
	'webserver' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-webserver to be present.')
}

CONFIGUREOPTS_remove = "--disable-static"
EXTRA_OECONF_append = " --enable-static"

do_configure() {
	cd ${S}/libraries/libapparmor
	aclocal
	autoconf --force
	libtoolize --automake -c --force
	automake -ac
	./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
	sed -i -e 's#^YACC.*#YACC := bison#' ${S}/parser/Makefile
	sed -i -e 's#^LEX.*#LEX := flex#' ${S}/parser/Makefile
}

do_compile () {
	oe_runmake -C ${B}/libraries/libapparmor
        oe_runmake -C ${B}/binutils
        oe_runmake -C ${B}/utils
        oe_runmake -C ${B}/parser
        oe_runmake -C ${B}/profiles

	if test -z "${HTTPD}" ; then
        	oe_runmake -C ${B}/changehat/mod_apparmor
	fi	

	if test -z "${PAMLIB}" ; then
        	oe_runmake -C ${B}/changehat/pam_apparmor
	fi
}

do_install () {
	install -d ${D}/${INIT_D_DIR}
	install -d ${D}/lib/apparmor
		
	oe_runmake -C ${B}/libraries/libapparmor DESTDIR="${D}" install
	oe_runmake -C ${B}/binutils DESTDIR="${D}" install
	oe_runmake -C ${B}/utils DESTDIR="${D}" install
	oe_runmake -C ${B}/parser DESTDIR="${D}" install
	oe_runmake -C ${B}/profiles DESTDIR="${D}" install

	if test -z "${HTTPD}" ; then
		oe_runmake -C ${B}/changehat/mod_apparmor DESTDIR="${D}" install
	fi

	if test -z "${PAMLIB}" ; then
		oe_runmake -C ${B}/changehat/pam_apparmor DESTDIR="${D}" install
	fi

	# aa-easyprof is installed by python-tools-setup.py, fix it up
	sed -i -e 's:/usr/bin/env.*:/usr/bin/python3:' ${D}${bindir}/aa-easyprof
	chmod 0755 ${D}${bindir}/aa-easyprof

	install ${WORKDIR}/apparmor ${D}/${INIT_D_DIR}/apparmor
	install ${WORKDIR}/functions ${D}/lib/apparmor
	if [ "${VIRTUAL-RUNTIME_init_manager}" = "systemd" ]; then
		install -d ${D}${systemd_system_unitdir}
		install ${WORKDIR}/apparmor.service \
			${D}${systemd_system_unitdir}
	fi
}

do_compile_ptest () {
        oe_runmake -C ${B}/tests/regression/apparmor
        oe_runmake -C ${B}/parser/tst
        oe_runmake -C ${B}/libraries/libapparmor
}

do_install_ptest () {
	t=${D}/${PTEST_PATH}/testsuite
	install -d ${t}
	install -d ${t}/tests/regression/apparmor
	cp -rf ${B}/tests/regression/apparmor ${t}/tests/regression

	install -d ${t}/parser/tst
	cp -rf ${B}/parser/tst ${t}/parser
	cp ${B}/parser/apparmor_parser ${t}/parser
	cp ${B}/parser/frob_slack_rc ${t}/parser

	install -d ${t}/libraries/libapparmor
	cp -rf ${B}/libraries/libapparmor ${t}/libraries

	install -d ${t}/common
	cp -rf ${B}/common ${t}

	install -d ${t}/binutils
	cp -rf ${B}/binutils ${t}
}

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "apparmor"
INITSCRIPT_PARAMS = "start 16 2 3 4 5 . stop 35 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "apparmor.service"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'apache2', 'mod-${PN}', '', d)}"

FILES_${PN} += "/lib/apparmor/ ${sysconfdir}/apparmor ${PYTHON_SITEPACKAGES_DIR}"
FILES_mod-${PN} = "${libdir}/apache2/modules/*"

ALLOW_EMPTY_${PN} = "1"

RDEPENDS_${PN} += "bash lsb"
RDEPENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG','python','python3 python3-modules','', d)}"
RDEPENDS_${PN}_remove += "${@bb.utils.contains('PACKAGECONFIG','perl','','perl', d)}"
RDEPENDS_${PN}-ptest += "perl coreutils dbus-lib"
