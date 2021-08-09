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

DEPENDS = "bison-native apr gettext-native coreutils-native swig-native"

SRC_URI = " \
    git://gitlab.com/apparmor/apparmor.git;protocol=https;branch=apparmor-3.0 \
    file://run-ptest \
    file://disable_perl_h_check.patch \
    file://crosscompile_perl_bindings.patch \
    file://0001-Makefile.am-suppress-perllocal.pod.patch \
    file://0001-Revert-profiles-Update-make-check-to-select-tools-ba.patch \
    file://0001-Makefile-fix-hardcoded-installation-directories.patch \
    file://0001-rc.apparmor.debian-add-missing-functions.patch \
    "

SRCREV = "b0f08aa9d678197b8e3477c2fbff790f50a1de5e"
S = "${WORKDIR}/git"

PARALLEL_MAKE = ""

COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"

inherit pkgconfig autotools-brokensep update-rc.d python3native python3targetconfig perlnative cpan systemd features_check bash-completion

REQUIRED_DISTRO_FEATURES = "apparmor"

PACKAGECONFIG ?= "python perl aa-decode"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages"
PACKAGECONFIG[python] = "--with-python, --without-python, python3 , python3-core python3-modules"
PACKAGECONFIG[perl] = "--with-perl, --without-perl, "
PACKAGECONFIG[apache2] = ",,apache2,"
PACKAGECONFIG[aa-decode] = ",,,bash"

python() {
    if 'apache2' in d.getVar('PACKAGECONFIG').split() and \
       'webserver' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-webserver to be present.')
}

DISABLE_STATIC = ""

do_configure() {
    cd ${S}/libraries/libapparmor
    aclocal
    autoconf --force
    libtoolize --automake -c --force
    automake -ac
    ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

do_compile () {
    sed -i "s@sed -ie 's///g' Makefile.perl@@" ${S}/libraries/libapparmor/swig/perl/Makefile
    oe_runmake -C ${B}/libraries/libapparmor
    oe_runmake -C ${B}/binutils
    oe_runmake -C ${B}/utils
    oe_runmake -C ${B}/parser
    oe_runmake -C ${B}/profiles

    if ${@bb.utils.contains('PACKAGECONFIG','apache2','true','false', d)}; then
        oe_runmake -C ${B}/changehat/mod_apparmor
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        oe_runmake -C ${B}/changehat/pam_apparmor
    fi
}

do_install () {
    oe_runmake -C ${B}/libraries/libapparmor DESTDIR="${D}" install
    oe_runmake -C ${B}/binutils DESTDIR="${D}" install
    oe_runmake -C ${B}/utils DESTDIR="${D}" install
    oe_runmake -C ${B}/parser DESTDIR="${D}" install
    oe_runmake -C ${B}/profiles DESTDIR="${D}" install

    if ! ${@bb.utils.contains('PACKAGECONFIG','aa-decode','true','false', d)}; then
        rm -f ${D}${sbindir}/aa-decode
    fi

    if ${@bb.utils.contains('PACKAGECONFIG','apache2','true','false', d)}; then
        oe_runmake -C ${B}/changehat/mod_apparmor DESTDIR="${D}" install
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        oe_runmake -C ${B}/changehat/pam_apparmor DESTDIR="${D}" install
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 755 ${B}/parser/rc.apparmor.debian ${D}${sysconfdir}/init.d/apparmor
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        oe_runmake -C ${B}/parser DESTDIR="${D}" install-systemd
    fi
}

#Building ptest on arm fails.
do_compile_ptest_aarch64 () {
  :
}

do_compile_ptest_arm () {
  :
}

do_compile_ptest () {
    sed -i -e 's/cpp \-dM/${HOST_PREFIX}gcc \-dM/' ${B}/tests/regression/apparmor/Makefile
    oe_runmake -C ${B}/tests/regression/apparmor USE_SYSTEM=0
    oe_runmake -C ${B}/libraries/libapparmor 
}

do_install_ptest () {
    t=${D}/${PTEST_PATH}/testsuite
    install -d ${t}
    install -d ${t}/tests/regression/apparmor
    cp -rf ${B}/tests/regression/apparmor ${t}/tests/regression

    cp ${B}/parser/apparmor_parser ${t}/parser
    cp ${B}/parser/frob_slack_rc ${t}/parser

    install -d ${t}/libraries/libapparmor
    cp -rf ${B}/libraries/libapparmor ${t}/libraries

    install -d ${t}/common
    cp -rf ${B}/common ${t}

    install -d ${t}/binutils
    cp -rf ${B}/binutils ${t}
}

#Building ptest on arm fails.
do_install_ptest_aarch64 () {
  :
}

do_install_ptest_arm() {
  :
}

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "apparmor"
INITSCRIPT_PARAMS = "start 16 2 3 4 5 . stop 35 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "apparmor.service"
SYSTEMD_AUTO_ENABLE ?= "enable"

PACKAGES += "mod-${PN}"

FILES_${PN} += "${nonarch_base_libdir}/apparmor/ ${base_libdir}/security/ ${sysconfdir}/apparmor ${nonarch_libdir}/${PYTHON_DIR}/site-packages"
FILES_mod-${PN} = "${libdir}/apache2/modules/*"
FILES_${PN}-dbg += "${base_libdir}/security/.debug"

DEPENDS_append_libc-musl = " fts "
RDEPENDS_${PN}_libc-musl +=  "musl-utils"
RDEPENDS_${PN}_libc-glibc +=  "glibc-utils"

# Add coreutils and findutils only if sysvinit scripts are in use
RDEPENDS_${PN} +=  "${@["coreutils findutils", ""][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'systemd')]} ${@bb.utils.contains('PACKAGECONFIG','python','python3-core python3-modules','', d)}"
RDEPENDS_${PN}_remove += "${@bb.utils.contains('PACKAGECONFIG','perl','','perl', d)}"
RDEPENDS_${PN}-ptest += "perl coreutils dbus-lib bash"
RDEPENDS_${PN} += "coreutils findutils"

INSANE_SKIP_${PN} = "ldflags"
PRIVATE_LIBS_${PN}-ptest = "libapparmor.so*"
