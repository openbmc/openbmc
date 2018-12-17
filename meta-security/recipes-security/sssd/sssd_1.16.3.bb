SUMMARY = "system security services daemon"
DESCRIPTION = "SSSD is a system security services daemon"
HOMEPAGE = "https://pagure.io/SSSD/sssd/"
SECTION = "base"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "openldap cyrus-sasl libtdb ding-libs libpam c-ares krb5 autoconf-archive"
DEPENDS += "libldb dbus libtalloc libpcre glib-2.0 popt e2fsprogs libtevent"

SRC_URI = "https://releases.pagure.org/SSSD/${BPN}/${BP}.tar.gz\
            file://sssd.conf "

SRC_URI[md5sum] = "af4288c9d1f9953e3b3b6e0b165a5ece"
SRC_URI[sha256sum] = "ee5d17a0c663c09819cbab9364085b9e57faeca02406cc30efe14cc0cfc04ec4"

inherit autotools pkgconfig gettext update-rc.d python-dir distro_features_check

REQUIRED_DISTRO_FEATURES = "pam"

CACHED_CONFIGUREVARS = "ac_cv_member_struct_ldap_conncb_lc_arg=no \
    ac_cv_path_NSUPDATE=${bindir} \
    ac_cv_path_PYTHON2=${PYTHON_DIR} ac_cv_prog_HAVE_PYTHON3=${PYTHON_DIR} \
    "

PACKAGECONFIG ?="nss nscd"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"

PACKAGECONFIG[ssh] = "--with-ssh, --with-ssh=no, "
PACKAGECONFIG[samba] = "--with-samba, --with-samba=no, samba"
PACKAGECONFIG[selinux] = "--with-selinux, --with-selinux=no --with-semanage=no, libselinux"
PACKAGECONFIG[manpages] = "--with-manpages, --with-manpages=no"
PACKAGECONFIG[python2] = "--with-python2-bindings, --without-python2-bindings"
PACKAGECONFIG[python3] = "--with-python3-bindings, --without-python3-bindings"
PACKAGECONFIG[nss] = "--with-crypto=nss, ,nss,"
PACKAGECONFIG[cyrpto] = "--with-crypto=libcrypto, , libcrypto"
PACKAGECONFIG[nscd] = "--with-nscd=${sbindir}, --with-nscd=no "
PACKAGECONFIG[nl] = "--with-libnl, --with-libnl=no, libnl"
PACKAGECONFIG[systemd] = "--with-systemdunitdir=${systemd_unitdir}/system/, --with-systemdunitdir="
PACKAGECONFIG[http] = "--with-secrets, --without-secrets, apache2"
PACKAGECONFIG[curl] = "--with-secrets --with-kcm, --without-secrets --without-kcm, curl"

EXTRA_OECONF += "--disable-cifs-idmap-plugin --without-nfsv4-idmapd-plugin --without-ipa-getkeytab"

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
}

CONFFILES_${PN} = "${sysconfdir}/${BPN}/${BPN}.conf"

INITSCRIPT_NAME = "sssd"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."
SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"

FILES_${PN} += "${libdir} ${datadir} /run ${libdir}/*.so* "
FILES_${PN}-dev = " ${includedir}/* ${libdir}/*la ${libdir}/*/*la"

# The package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"

RDEPENDS_${PN} += "bind dbus"
