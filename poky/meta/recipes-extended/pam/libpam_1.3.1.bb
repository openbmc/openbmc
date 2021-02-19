DISABLE_STATIC = ""
SUMMARY = "Linux-PAM (Pluggable Authentication Modules)"
DESCRIPTION = "Linux-PAM (Pluggable Authentication Modules for Linux), a flexible mechanism for authenticating users"
HOMEPAGE = "https://fedorahosted.org/linux-pam/"
BUGTRACKER = "https://fedorahosted.org/linux-pam/newticket"
SECTION = "base"
# PAM is dual licensed under GPL and BSD.
# /etc/pam.d comes from Debian libpam-runtime in 2009-11 (at that time
# libpam-runtime-1.0.1 is GPLv2+), by openembedded
LICENSE = "GPLv2+ | BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=7eb5c1bf854e8881005d673599ee74d3 \
                    file://libpamc/License;md5=a4da476a14c093fdc73be3c3c9ba8fb3 \
                    "

SRC_URI = "https://github.com/linux-pam/linux-pam/releases/download/v${PV}/Linux-PAM-${PV}.tar.xz \
           file://99_pam \
           file://pam.d/common-account \
           file://pam.d/common-auth \
           file://pam.d/common-password \
           file://pam.d/common-session \
           file://pam.d/common-session-noninteractive \
           file://pam.d/other \
           file://libpam-xtests.patch \
           file://pam-security-abstract-securetty-handling.patch \
           file://pam-unix-nullok-secure.patch \
           file://crypt_configure.patch \
          "

SRC_URI[md5sum] = "558ff53b0fc0563ca97f79e911822165"
SRC_URI[sha256sum] = "eff47a4ecd833fbf18de9686632a70ee8d0794b79aecb217ebd0ce11db4cd0db"

DEPENDS = "bison-native flex flex-native cracklib libxml2-native virtual/crypt"

EXTRA_OECONF = "--includedir=${includedir}/security \
                --libdir=${base_libdir} \
                --disable-nis \
                --disable-regenerate-docu \
		--disable-prelude"

CFLAGS_append = " -fPIC "

S = "${WORKDIR}/Linux-PAM-${PV}"

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[audit] = "--enable-audit,--disable-audit,audit,"
PACKAGECONFIG[userdb] = "--enable-db=db,--enable-db=no,db,"

PACKAGES += "${PN}-runtime ${PN}-xtests"
FILES_${PN} = "${base_libdir}/lib*${SOLIBS}"
FILES_${PN}-dev += "${base_libdir}/security/*.la ${base_libdir}/*.la ${base_libdir}/lib*${SOLIBSDEV}"
FILES_${PN}-runtime = "${sysconfdir}"
FILES_${PN}-xtests = "${datadir}/Linux-PAM/xtests"

PACKAGES_DYNAMIC += "^${MLPREFIX}pam-plugin-.*"

def get_multilib_bit(d):
    baselib = d.getVar('baselib') or ''
    return baselib.replace('lib', '')

libpam_suffix = "suffix${@get_multilib_bit(d)}"

RPROVIDES_${PN} += "${PN}-${libpam_suffix}"
RPROVIDES_${PN}-runtime += "${PN}-runtime-${libpam_suffix}"

RDEPENDS_${PN}-runtime = "${PN}-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-deny-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-permit-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-warn-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-unix-${libpam_suffix} \
    "
RDEPENDS_${PN}-xtests = "${PN}-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-access-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-debug-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-cracklib-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-pwhistory-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-succeed-if-${libpam_suffix} \
    ${MLPREFIX}pam-plugin-time-${libpam_suffix} \
    coreutils"

# FIXME: Native suffix breaks here, disable it for now
RRECOMMENDS_${PN} = "${PN}-runtime-${libpam_suffix}"
RRECOMMENDS_${PN}_class-native = ""

python populate_packages_prepend () {
    def pam_plugin_append_file(pn, dir, file):
        nf = os.path.join(dir, file)
        of = d.getVar('FILES_' + pn)
        if of:
            nf = of + " " + nf
        d.setVar('FILES_' + pn, nf)

    def pam_plugin_hook(file, pkg, pattern, format, basename):
        pn = d.getVar('PN')
        libpam_suffix = d.getVar('libpam_suffix')

        rdeps = d.getVar('RDEPENDS_' + pkg)
        if rdeps:
            rdeps = rdeps + " " + pn + "-" + libpam_suffix
        else:
            rdeps = pn + "-" + libpam_suffix
        d.setVar('RDEPENDS_' + pkg, rdeps)

        provides = d.getVar('RPROVIDES_' + pkg)
        if provides:
            provides = provides + " " + pkg + "-" + libpam_suffix
        else:
            provides = pkg + "-" + libpam_suffix
        d.setVar('RPROVIDES_' + pkg, provides)

    mlprefix = d.getVar('MLPREFIX') or ''
    dvar = d.expand('${WORKDIR}/package')
    pam_libdir = d.expand('${base_libdir}/security')
    pam_sbindir = d.expand('${sbindir}')
    pam_filterdir = d.expand('${base_libdir}/security/pam_filter')
    pam_pkgname = mlprefix + 'pam-plugin%s'

    do_split_packages(d, pam_libdir, r'^pam(.*)\.so$', pam_pkgname,
                      'PAM plugin for %s', hook=pam_plugin_hook, extra_depends='')
    pam_plugin_append_file('%spam-plugin-unix' % mlprefix, pam_sbindir, 'unix_chkpwd')
    pam_plugin_append_file('%spam-plugin-unix' % mlprefix, pam_sbindir, 'unix_update')
    pam_plugin_append_file('%spam-plugin-tally' % mlprefix, pam_sbindir, 'pam_tally')
    pam_plugin_append_file('%spam-plugin-tally2' % mlprefix, pam_sbindir, 'pam_tally2')
    pam_plugin_append_file('%spam-plugin-timestamp' % mlprefix, pam_sbindir, 'pam_timestamp_check')
    pam_plugin_append_file('%spam-plugin-mkhomedir' % mlprefix, pam_sbindir, 'mkhomedir_helper')
    pam_plugin_append_file('%spam-plugin-console' % mlprefix, pam_sbindir, 'pam_console_apply')
    do_split_packages(d, pam_filterdir, r'^(.*)$', 'pam-filter-%s', 'PAM filter for %s', extra_depends='')
}

do_compile_ptest() {
        cd tests
        sed -i -e 's/$(MAKE) $(AM_MAKEFLAGS) check-TESTS//' Makefile
        oe_runmake check-am
        cd -
}

do_install() {
	autotools_do_install

	# don't install /var/run when populating rootfs. Do it through volatile
	rm -rf ${D}${localstatedir}
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/99_pam ${D}${sysconfdir}/default/volatiles

	install -d ${D}${sysconfdir}/pam.d/
	install -m 0644 ${WORKDIR}/pam.d/* ${D}${sysconfdir}/pam.d/

	# The lsb requires unix_chkpwd has setuid permission
	chmod 4755 ${D}${sbindir}/unix_chkpwd

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		echo "session optional pam_systemd.so" >> ${D}${sysconfdir}/pam.d/common-session
	fi
}

do_install_ptest() {
    if [ ${PTEST_ENABLED} = "1" ]; then
        mkdir -p ${D}${PTEST_PATH}/tests
        install -m 0755 ${B}/tests/.libs/* ${D}${PTEST_PATH}/tests
    fi
}

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

BBCLASSEXTEND = "nativesdk native"

CONFFILES_${PN}-runtime += "${sysconfdir}/pam.d/common-session"
CONFFILES_${PN}-runtime += "${sysconfdir}/pam.d/common-auth"
CONFFILES_${PN}-runtime += "${sysconfdir}/pam.d/common-password"
CONFFILES_${PN}-runtime += "${sysconfdir}/pam.d/common-session-noninteractive"
CONFFILES_${PN}-runtime += "${sysconfdir}/pam.d/common-account"
CONFFILES_${PN}-runtime += "${sysconfdir}/security/limits.conf"

UPSTREAM_CHECK_URI = "https://github.com/linux-pam/linux-pam/releases"

CVE_PRODUCT = "linux-pam"
