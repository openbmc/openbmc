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
    file://disable_perl_h_check.patch \
    file://crosscompile_perl_bindings.patch \
    file://apparmor.rc \
    file://functions \
    file://apparmor \
    file://apparmor.service \
    file://0001-Makefile.am-suppress-perllocal.pod.patch \
    file://run-ptest \
    file://0001-apparmor-fix-manpage-order.patch \
    file://0001-Revert-profiles-Update-make-check-to-select-tools-ba.patch \
    file://0001-libapparmor-add-missing-include-for-socklen_t.patch \
    file://0002-libapparmor-add-aa_features_new_from_file-to-public-.patch \
    file://0003-libapparmor-add-_aa_asprintf-to-private-symbols.patch \
    file://0001-aa_status-Fix-build-issue-with-musl.patch \
    file://0001-parser-Makefile-dont-force-host-cpp-to-detect-reallo.patch \
    "

SRCREV = "5d51483bfecf556183558644dc8958135397a7e2"
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
    install -d ${D}/${INIT_D_DIR}
    install -d ${D}/lib/apparmor
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
        install -d ${D}/lib/security
        oe_runmake -C ${B}/changehat/pam_apparmor DESTDIR="${D}" install
    fi

    install -m 755 ${WORKDIR}/apparmor ${D}/${INIT_D_DIR}/apparmor
    install -m 755 ${WORKDIR}/functions ${D}/lib/apparmor

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/apparmor.service ${D}${systemd_system_unitdir}
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

pkg_postinst_ontarget_${PN} () {
if [ ! -d /etc/apparmor.d/cache ] ; then
    mkdir /etc/apparmor.d/cache
fi
}

# We need the init script so don't rm it
RMINITDIR_class-target_remove = " rm_sysvinit_initddir"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "apparmor"
INITSCRIPT_PARAMS = "start 16 2 3 4 5 . stop 35 0 1 6 ."

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "apparmor.service"
SYSTEMD_AUTO_ENABLE ?= "enable"

PACKAGES += "mod-${PN}"

FILES_${PN} += "/lib/apparmor/ /lib/security/ ${sysconfdir}/apparmor ${PYTHON_SITEPACKAGES_DIR}"
FILES_mod-${PN} = "${libdir}/apache2/modules/*"

DEPENDS_append_libc-musl = " fts "
RDEPENDS_${PN}_libc-musl +=  "musl-utils"
RDEPENDS_${PN}_libc-glibc +=  "glibc-utils"

# Add coreutils and findutils only if sysvinit scripts are in use
RDEPENDS_${PN} +=  "${@["coreutils findutils", ""][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'systemd')]} ${@bb.utils.contains('PACKAGECONFIG','python','python3-core python3-modules','', d)}"
RDEPENDS_${PN}_remove += "${@bb.utils.contains('PACKAGECONFIG','perl','','perl', d)}"
RDEPENDS_${PN}-ptest += "perl coreutils dbus-lib bash"

INSANE_SKIP_${PN} = "ldflags"
PRIVATE_LIBS_${PN}-ptest = "libapparmor.so*"
