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
           file://no_gen.patch \
           file://fix_gid.patch \
           file://drop_ntpdate_chk.patch \
           file://fix-ldblibdir.patch \
           file://musl_fixup.patch \
           "
SRC_URI[sha256sum] = "e8aa5e6b48ae465bea7064048715ce7e9c53b50ec6a9c69304f59e0d35be40ff"

UPSTREAM_CHECK_URI = "https://github.com/SSSD/${BPN}/releases"

inherit autotools pkgconfig gettext python3native features_check systemd useradd

REQUIRED_DISTRO_FEATURES = "pam"

SSSD_UID ?= "sssd"
SSSD_GID ?= "sssd"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system sssd"
USERADD_PARAM:${PN} = "--system --home /run/sssd --no-create-home -g sssd --shell /sbin/nologin sssd"

CACHED_CONFIGUREVARS = "ac_cv_member_struct_ldap_conncb_lc_arg=no \
    ac_cv_prog_HAVE_PYTHON3=yes \
    "

PACKAGECONFIG ?= "nss autofs sudo"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[autofs] = "--with-autofs, --with-autofs=no"
PACKAGECONFIG[crypto] = ", , libcrypto"
PACKAGECONFIG[curl] = "--with-kcm, --without-kcm, curl jansson"
PACKAGECONFIG[manpages] = "--with-manpages, --with-manpages=no, libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[nl] = "--with-libnl, --with-libnl=no, libnl"
PACKAGECONFIG[nss] = ", ,nss,"
PACKAGECONFIG[oidc_child] = "--with-oidc-child, --without-oidc-child"
PACKAGECONFIG[python3] = "--with-python3-bindings, --without-python3-bindings python3dir=${PYTHON_SITEPACKAGES_DIR}, python3-setuptools-native"
PACKAGECONFIG[samba] = "--with-samba, --with-samba=no, samba"
PACKAGECONFIG[selinux] = "--with-selinux, --with-selinux=no, libselinux"
PACKAGECONFIG[ssh] = "--with-ssh, --with-ssh=no, "
PACKAGECONFIG[sudo] = "--with-sudo, --with-sudo=no, "
PACKAGECONFIG[systemd] = "--with-initscript=systemd --with-systemdunitdir=${systemd_system_unitdir} --with-systemdconfdir=${sysconfdir}/systemd/system, --with-initscript=sysv,,python3-systemd"

EXTRA_OECONF += " \
    --disable-cifs-idmap-plugin \
    --without-nfsv4-idmapd-plugin \
    --without-ipa-getkeytab \
    --without-python2-bindings \
    --enable-pammoddir=${base_libdir}/security \
    --with-xml-catalog-path=${STAGING_ETCDIR_NATIVE}/xml/catalog \
    --with-pid-path=/run/sssd \
    --with-os=fedora \
    --with-sssd-user=sssd \
"

do_configure:prepend () {
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
    install -m 600 ${UNPACKDIR}/${BPN}.conf ${D}/${sysconfdir}/${BPN}
    chown -R root:${SSSD_GID} ${D}/${sysconfdir}/${BPN}

    # /var/log/sssd needs to be created in runtime. Use rmdir to catch if
    # upstream stops creating /var/log/sssd, or adds something else in
    # /var/log.
    rmdir ${D}${localstatedir}/log/${BPN} ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/log/sssd 0750 ${SSSD_UID} ${SSSD_GID} - -" > ${D}${sysconfdir}/tmpfiles.d/sssd.conf
        echo "d /run/sssd 0750 ${SSSD_UID} ${SSSD_GID} - -" >> ${D}${sysconfdir}/tmpfiles.d/sssd.conf
    fi

    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d ${SSSD_UID}:${SSSD_GID} 0750 ${localstatedir}/log/sssd none" > ${D}${sysconfdir}/default/volatiles/99_sssd
        echo "d ${SSSD_UID}:${SSSD_GID} 0750 ${localstatedir}/run/sssd none" >> ${D}${sysconfdir}/default/volatiles/99_sssd
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'true', 'false', d)}; then
        sed '1s,/usr/bin/python,/usr/bin/python3,' -i ${D}${sbindir}/sss_obfuscate
    fi

    # Remove /run as it is created on startup
    rm -rf ${D}/run ${D}/var/run
}

pkg_postinst_ontarget:${PN} () {
    if [ -e /etc/init.d/populate-volatile.sh ] ; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
}

CONFFILES:${PN} = "${sysconfdir}/${BPN}/${BPN}.conf"

INITSCRIPT_NAME = "sssd"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."
SYSTEMD_SERVICE:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'autofs', 'sssd-autofs.service sssd-autofs.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'curl', 'sssd-kcm.service sssd-kcm.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'ssh', 'sssd-ssh.service sssd-ssh.socket', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'sudo', 'sssd-sudo.service sssd-sudo.socket', '', d)} \
    sssd-ifp.service \
    sssd-nss.service \
    sssd-nss.socket \
    sssd-pam.service \
    sssd-pam.socket \
    sssd.service \
"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGES =+ "sssd-python libsss-sudo"
ALLOW_EMPTY:libsss-sudo = "1"

FILES:${PN} += "${base_libdir}/security/pam_sss*.so  \
                ${nonarch_libdir}/tmpfiles.d \
                ${datadir}/dbus-1/system.d/*.conf \
                ${datadir}/dbus-1/system-services/*.service \
                ${datadir}/polkit-1/* \
                ${libdir}/krb5/* \
                ${libdir}/ldb/* \
                ${PYTHON_SITEPACKAGES_DIR}/sssd \
                "

FILES:${PN}-python = "${sbindir}/sss_obfuscate \
                      ${PYTHON_SITEPACKAGES_DIR} \
                      "
FILES:libsss-sudo = "${libdir}/libsss_sudo.so"

RDEPENDS:${PN} = "bind \
                  bind-utils \
                  dbus \
                  libldb \
                  libpam \
                  libsss-sudo \
                  python3-core \
                  python3-logging \
                  "
RDEPENDS:${PN}-python = "python3-core"
