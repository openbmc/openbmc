SUMMARY = "system security services daemon"
DESCRIPTION = "SSSD is a system security services daemon"
HOMEPAGE = "https://pagure.io/SSSD/sssd/"
SECTION = "base"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "acl attr openldap cyrus-sasl libtdb ding-libs libpam c-ares krb5 autoconf-archive"
DEPENDS:append = " libldb dbus libtalloc libpcre glib-2.0 popt e2fsprogs libtevent bind p11-kit"

DEPENDS:append:libc-musl = " musl-nscd"

# If no crypto has been selected, default to DEPEND on nss, since that's what
# sssd will pick if no active choice is made during configure
DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'nss', '', \
               bb.utils.contains('PACKAGECONFIG', 'crypto', '', 'nss', d), d)}"

SRC_URI = "https://github.com/SSSD/sssd/releases/download/${PV}/sssd-${PV}.tar.gz \
           file://sssd.conf \
           file://volatiles.99_sssd \
           file://no_gen.patch \
           file://fix_gid.patch \
           file://drop_ntpdate_chk.patch \
           file://fix-ldblibdir.patch \
           file://musl_fixup.patch \
           file://CVE-2021-3621.patch \
           "

SRC_URI[sha256sum] = "5e21b3c7b4a2f1063d0fbdd3216d29886b6eaba153b44fb5961698367f399a0f"

inherit autotools pkgconfig gettext python3-dir features_check systemd

REQUIRED_DISTRO_FEATURES = "pam"

SSSD_UID ?= "root"
SSSD_GID ?= "root"

CACHED_CONFIGUREVARS = "ac_cv_member_struct_ldap_conncb_lc_arg=no \
    ac_cv_prog_HAVE_PYTHON3=${PYTHON_DIR} \
    "

PACKAGECONFIG ?="nss nscd autofs sudo infopipe"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[autofs] = "--with-autofs, --with-autofs=no"
PACKAGECONFIG[crypto] = ", , libcrypto"
PACKAGECONFIG[curl] = "--with-kcm, --without-kcm, curl jansson"
PACKAGECONFIG[infopipe] = "--with-infopipe, --with-infopipe=no, "
PACKAGECONFIG[manpages] = "--with-manpages, --with-manpages=no, libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[nl] = "--with-libnl, --with-libnl=no, libnl"
PACKAGECONFIG[nscd] = "--with-nscd=${sbindir}, --with-nscd=no "
PACKAGECONFIG[nss] = ", ,nss,"
PACKAGECONFIG[python3] = "--with-python3-bindings, --without-python3-bindings"
PACKAGECONFIG[samba] = "--with-samba, --with-samba=no, samba"
PACKAGECONFIG[selinux] = "--with-selinux, --with-selinux=no --with-semanage=no, libselinux"
PACKAGECONFIG[ssh] = "--with-ssh, --with-ssh=no, "
PACKAGECONFIG[sudo] = "--with-sudo, --with-sudo=no, "
PACKAGECONFIG[systemd] = "--with-initscript=systemd,--with-initscript=sysv"

EXTRA_OECONF += " \
    --disable-cifs-idmap-plugin \
    --without-nfsv4-idmapd-plugin \
    --without-ipa-getkeytab \
    --without-python2-bindings \
    --enable-pammoddir=${base_libdir}/security \
    --without-python2-bindings \
    --without-secrets \
    --with-xml-catalog-path=${STAGING_ETCDIR_NATIVE}/xml/catalog \
    --with-pid-path=/run \
"

do_configure:prepend() {
    mkdir -p ${AUTOTOOLS_AUXDIR}/build
    cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${AUTOTOOLS_AUXDIR}/build/

    # libresove has host path, remove it
    sed -i -e "s#\$sss_extra_libdir##" ${S}/src/external/libresolv.m4
}

do_compile:prepend () {
     echo '#define NSUPDATE_PATH "${bindir}"' >> ${B}/config.h
}
do_install () {
    oe_runmake install  DESTDIR="${D}"
    rmdir --ignore-fail-on-non-empty "${D}/${bindir}"
    install -d ${D}/${sysconfdir}/${BPN}
    install -m 600 ${WORKDIR}/${BPN}.conf ${D}/${sysconfdir}/${BPN}

    # /var/log/sssd needs to be created in runtime. Use rmdir to catch if
    # upstream stops creating /var/log/sssd, or adds something else in
    # /var/log.
    rmdir ${D}${localstatedir}/log/${BPN} ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/log/sssd 0750 - - - -" > ${D}${sysconfdir}/tmpfiles.d/sss.conf
    fi

    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d ${SSSD_UID}:${SSSD_GID} 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi

    # Remove /run as it is created on startup
    rm -rf ${D}/run

    rm -f ${D}${systemd_system_unitdir}/sssd-secrets.*
}

pkg_postinst_ontarget:${PN} () {
if [ -e /etc/init.d/populate-volatile.sh ] ; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
    chown ${SSSD_UID}:${SSSD_GID} ${sysconfdir}/${BPN}/${BPN}.conf
}

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"

CONFFILES:${PN} = "${sysconfdir}/${BPN}/${BPN}.conf"

INITSCRIPT_NAME = "sssd"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."
SYSTEMD_SERVICE:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'autofs', 'sssd-autofs.service sssd-autofs.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'curl', 'sssd-kcm.service sssd-kcm.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'infopipe', 'sssd-ifp.service ', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'ssh', 'sssd-ssh.service sssd-ssh.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'sudo', 'sssd-sudo.service sssd-sudo.socket', '', d)} \
    sssd-nss.service \
    sssd-nss.socket \
    sssd-pam-priv.socket \
    sssd-pam.service \
    sssd-pam.socket \
    sssd.service \
"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGES =+ "libsss-sudo"
ALLOW_EMPTY:libsss-sudo = "1"

FILES:${PN} += "${base_libdir}/security/pam_sss*.so  \
                ${datadir}/dbus-1/system-services/*.service \
                ${libdir}/krb5/* \
                ${libdir}/ldb/* \
                "
FILES:libsss-sudo = "${libdir}/libsss_sudo.so"

RDEPENDS:${PN} = "bind bind-utils dbus libldb libpam libsss-sudo"
