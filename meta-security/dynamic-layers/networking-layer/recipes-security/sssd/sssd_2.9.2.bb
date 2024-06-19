SUMMARY = "system security services daemon"
DESCRIPTION = "SSSD is a system security services daemon"
HOMEPAGE = "https://pagure.io/SSSD/sssd/"
SECTION = "base"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "acl attr cyrus-sasl libtdb ding-libs libpam c-ares krb5 autoconf-archive"
DEPENDS:append = " libldb dbus libtalloc libpcre2 glib-2.0 popt e2fsprogs libtevent"
DEPENDS:append = " openldap bind p11-kit jansson softhsm openssl libunistring"

DEPENDS:append:libc-musl = " musl-nscd"

# If no crypto has been selected, default to DEPEND on nss, since that's what
# sssd will pick if no active choice is made during configure
DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'nss', '', \
               bb.utils.contains('PACKAGECONFIG', 'crypto', '', 'nss', d), d)}"

SRC_URI = "https://github.com/SSSD/sssd/releases/download/${PV}/${BP}.tar.gz \
           file://sssd.conf \
           file://volatiles.99_sssd \
           file://no_gen.patch \
           file://fix_gid.patch \
           file://drop_ntpdate_chk.patch \
           file://fix-ldblibdir.patch \
           file://musl_fixup.patch \
           file://0001-sssctl-add-error-analyzer.patch \
           "
SRC_URI[sha256sum] = "827bc65d64132410e6dd3df003f04829d60387ec30e72b2d4e22d93bb6f762ba"

UPSTREAM_CHECK_URI = "https://github.com/SSSD/${BPN}/releases"

inherit autotools pkgconfig gettext python3-dir features_check systemd

REQUIRED_DISTRO_FEATURES = "pam"

SSSD_UID ?= "root"
SSSD_GID ?= "root"

CACHED_CONFIGUREVARS = "ac_cv_member_struct_ldap_conncb_lc_arg=no \
    ac_cv_prog_HAVE_PYTHON3=${PYTHON_DIR} \
    "

PACKAGECONFIG ?="nss autofs sudo infopipe"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[autofs] = "--with-autofs, --with-autofs=no"
PACKAGECONFIG[crypto] = ", , libcrypto"
PACKAGECONFIG[curl] = "--with-kcm, --without-kcm, curl jansson"
PACKAGECONFIG[infopipe] = "--with-infopipe, --with-infopipe=no, "
PACKAGECONFIG[manpages] = "--with-manpages, --with-manpages=no, libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[nl] = "--with-libnl, --with-libnl=no, libnl"
PACKAGECONFIG[nss] = ", ,nss,"
PACKAGECONFIG[oidc_child] = "--with-oidc-child, --without-oidc-child"
PACKAGECONFIG[python3] = "--with-python3-bindings, --without-python3-bindings"
PACKAGECONFIG[samba] = "--with-samba, --with-samba=no, samba"
PACKAGECONFIG[selinux] = "--with-selinux, --with-selinux=no --with-semanage=no, libselinux"
PACKAGECONFIG[ssh] = "--with-ssh, --with-ssh=no, "
PACKAGECONFIG[sudo] = "--with-sudo, --with-sudo=no, "
PACKAGECONFIG[systemd] = "--with-initscript=systemd,--with-initscript=sysv,,python3-systemd"

EXTRA_OECONF += " \
    --disable-cifs-idmap-plugin \
    --without-nfsv4-idmapd-plugin \
    --without-ipa-getkeytab \
    --without-python2-bindings \
    --enable-pammoddir=${base_libdir}/security \
    --with-xml-catalog-path=${STAGING_ETCDIR_NATIVE}/xml/catalog \
    --with-pid-path=/run \
"

do_configure:prepend() {
    mkdir -p ${AUTOTOOLS_AUXDIR}/build
    cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${AUTOTOOLS_AUXDIR}/build/

    # additional_libdir  defaults to /usr/lib so replace with staging_libdir globally
    sed -i -e "s#\$additional_libdir#\${STAGING_LIBDIR}#" ${S}/src/build_macros.m4
}

do_compile:prepend () {
     sed -i -e "s/__useconds_t/useconds_t/g" ${S}/src/tools/tools_mc_util.c
     echo '#define NSUPDATE_PATH "${bindir}"' >> ${B}/config.h
}
do_install () {
    oe_runmake install  DESTDIR="${D}"
    rmdir --ignore-fail-on-non-empty "${D}/${bindir}"

    install -d ${D}/${sysconfdir}/${BPN}
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}
    mv ${D}/${BPN}  ${D}/${PYTHON_SITEPACKAGES_DIR}

    install -m 600 ${UNPACKDIR}/${BPN}.conf ${D}/${sysconfdir}/${BPN}

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

#    rm -fr ${D}/sssd
    rm -f ${D}${systemd_system_unitdir}/sssd-secrets.*
}

pkg_postinst_ontarget:${PN} () {
if [ -e /etc/init.d/populate-volatile.sh ] ; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
    chown ${SSSD_UID}:${SSSD_GID} ${sysconfdir}/${BPN}/${BPN}.conf
}

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
                ${nonarch_libdir}/tmpfiles.d \
                ${datadir}/dbus-1/system.d/*.conf \
                ${datadir}/dbus-1/system-services/*.service \
                ${libdir}/krb5/* \
                ${libdir}/ldb/* \
                ${PYTHON_SITEPACKAGES_DIR}/sssd \
                "

FILES:libsss-sudo = "${libdir}/libsss_sudo.so"

RDEPENDS:${PN} = "bind bind-utils dbus libldb libpam libsss-sudo"
