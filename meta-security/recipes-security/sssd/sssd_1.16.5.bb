SUMMARY = "system security services daemon"
DESCRIPTION = "SSSD is a system security services daemon"
HOMEPAGE = "https://pagure.io/SSSD/sssd/"
SECTION = "base"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "openldap cyrus-sasl libtdb ding-libs libpam c-ares krb5 autoconf-archive"
DEPENDS_append = " libldb dbus libtalloc libpcre glib-2.0 popt e2fsprogs libtevent"

DEPENDS_append_libc-musl = " musl-nscd"

# If no crypto has been selected, default to DEPEND on nss, since that's what
# sssd will pick if no active choice is made during configure
DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'nss', '', \
               bb.utils.contains('PACKAGECONFIG', 'crypto', '', 'nss', d), d)}"

SRC_URI = "https://releases.pagure.org/SSSD/${BPN}/${BP}.tar.gz \
           file://sssd.conf \
           file://volatiles.99_sssd \
           file://fix-ldblibdir.patch \
           file://0001-build-Don-t-use-AC_CHECK_FILE-when-building-manpages.patch \
           file://0001-nss-Collision-with-external-nss-symbol.patch \
           file://0002-Provide-missing-defines-which-otherwise-are-availabl.patch \
           "

SRC_URI[sha256sum] = "2e1a7bf036b583f686d35164f2d79bdf4857b98f51fe8b0d17aa0fa756e4d0c0"

inherit autotools pkgconfig gettext python3-dir features_check systemd

REQUIRED_DISTRO_FEATURES = "pam"

SSSD_UID ?= "root"
SSSD_GID ?= "root"

CACHED_CONFIGUREVARS = "ac_cv_member_struct_ldap_conncb_lc_arg=no \
    ac_cv_path_NSUPDATE=${bindir} ac_cv_prog_HAVE_PYTHON3=${PYTHON_DIR} \
    "

PACKAGECONFIG ?="nss nscd autofs sudo infopipe"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[autofs] = "--with-autofs, --with-autofs=no"
PACKAGECONFIG[crypto] = "--with-crypto=libcrypto, , libcrypto"
PACKAGECONFIG[curl] = "--with-kcm, --without-kcm, curl jansson"
PACKAGECONFIG[infopipe] = "--with-infopipe, --with-infopipe=no, "
PACKAGECONFIG[manpages] = "--with-manpages, --with-manpages=no, libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[nl] = "--with-libnl, --with-libnl=no, libnl"
PACKAGECONFIG[nscd] = "--with-nscd=${sbindir}, --with-nscd=no "
PACKAGECONFIG[nss] = "--with-crypto=nss, ,nss,"
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
"

do_configure_prepend() {
    mkdir -p ${AUTOTOOLS_AUXDIR}/build
    cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${AUTOTOOLS_AUXDIR}/build/

    # libresove has host path, remove it
    sed -i -e "s#\$sss_extra_libdir##" ${S}/src/external/libresolv.m4
}

do_install () {
    oe_runmake install  DESTDIR="${D}"
    rmdir --ignore-fail-on-non-empty "${D}/${bindir}"
    install -d ${D}/${sysconfdir}/${BPN}
    install -m 600 ${WORKDIR}/${BPN}.conf ${D}/${sysconfdir}/${BPN}
    install -D -m 644 ${WORKDIR}/volatiles.99_sssd ${D}/${sysconfdir}/default/volatiles/99_sssd

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/log/sssd 0750 - - - -" > ${D}${sysconfdir}/tmpfiles.d/sss.conf
    fi

    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    rm -f ${D}${systemd_system_unitdir}/sssd-secrets.*
}

pkg_postinst_ontarget_${PN} () {
if [ -e /etc/init.d/populate-volatile.sh ] ; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
    chown ${SSSD_UID}:${SSSD_GID} ${sysconfdir}/${BPN}/${BPN}.conf
}

CONFFILES_${PN} = "${sysconfdir}/${BPN}/${BPN}.conf"

INITSCRIPT_NAME = "sssd"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."
SYSTEMD_SERVICE_${PN} = " \
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

FILES_${PN} += "${libdir} ${datadir} ${base_libdir}/security/pam_sss.so"
FILES_${PN}-dev = " ${includedir}/* ${libdir}/*la ${libdir}/*/*la"

# The package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"

RDEPENDS_${PN} = "bind dbus libldb libpam"
