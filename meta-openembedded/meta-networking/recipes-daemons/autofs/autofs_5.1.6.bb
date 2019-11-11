SUMMARY = "Kernel based automounter for linux"
SECTION = "utils"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS += "libtirpc flex-native bison-native e2fsprogs openssl libxml2 util-linux cyrus-sasl libnsl2"

CFLAGS += "-I${STAGING_INCDIR}/tirpc"

inherit autotools-brokensep systemd update-rc.d pkgconfig

SRC_URI = "${KERNELORG_MIRROR}/linux/daemons/autofs/v5/autofs-${PV}.tar.gz \
           file://autofs-5.0.7-include-linux-nfs.h-directly-in-rpc_sub.patch \
           file://no-bash.patch \
           file://cross.patch \
           file://fix_disable_ldap.patch \
           file://autofs-5.0.7-fix-lib-deps.patch \
           file://add-the-needed-stdarg.h.patch \
           file://using-pkg-config-to-detect-libxml-2.0-and-krb5.patch \
           file://force-STRIP-to-emtpy.patch \
           file://remove-bashism.patch \
           file://fix-the-YACC-rule-to-fix-a-building-failure.patch \
           file://0001-Define-__SWORD_TYPE-and-_PATH_NSSWITCH_CONF.patch \
           file://0002-Replace-__S_IEXEC-with-S_IEXEC.patch \
           file://pkgconfig-libnsl.patch \
           file://0001-modules-lookup_multi.c-Replace-__S_IEXEC-with-S_IEXE.patch \
           file://0001-Do-not-hardcode-path-for-pkg.m4.patch \
           file://0001-Bug-fix-for-pid_t-not-found-on-musl.patch \
           "


SRC_URI[md5sum] = "e6800e0afd6009ecdff148088c564050"
SRC_URI[sha256sum] = "82094cad44f4e5c4f93eff2789cd66b57d7ab3fa646b7722d97608571001e694"

UPSTREAM_CHECK_URI = "${KERNELORG_MIRROR}/linux/daemons/autofs/v5/"

INITSCRIPT_NAME = "autofs"
INITSCRIPT_PARAMS = "defaults"

# FIXME: modules/Makefile has crappy rules that don't obey LDFLAGS
#CFLAGS += "${LDFLAGS}"

PACKAGECONFIG[systemd] = "--with-systemd=${systemd_unitdir}/system,--without-systemd,systemd"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

EXTRA_OEMAKE = "DONTSTRIP=1"
EXTRA_OECONF += "--disable-mount-locking \
                --enable-ignore-busy --with-openldap=no \
                --with-confdir=${sysconfdir}/default \
                --with-fifodir=/run \
                --with-flagdir=/run \
                --with-sasl=no --with-libtirpc \
                --with-mapdir=${sysconfdir} \
                --with-path=${STAGING_BINDIR_NATIVE} \
                --with-fifodir=${localstatedir}/run \
                --with-flagdir=${localstatedir}/run \
"
CACHED_CONFIGUREVARS = "ac_cv_path_RANLIB=${RANLIB} \
                        ac_cv_path_RPCGEN=rpcgen \
                        initdir=${INIT_D_DIR} \
                        piddir=/run \
"

do_configure_prepend () {
    if [ ! -e ${S}/acinclude.m4 ]; then
        cp ${S}/aclocal.m4 ${S}/acinclude.m4
    fi
    cp ${STAGING_DATADIR_NATIVE}/aclocal/pkg.m4 .
}

do_install_append () {
    # samples have been removed from SUBDIRS from 5.1.5, need to install separately
    oe_runmake 'DESTDIR=${D}' install_samples

    if [ -d ${D}/run ]; then
        rmdir ${D}/run
    fi
    if [ -d ${D}${localstatedir}/run ]; then
        rmdir ${D}${localstatedir}/run
    fi
    # On hybrid systemd/sysvinit builds, we need to install the sysvinit script by hand.
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d -m 755 ${D}${INIT_D_DIR}
        install -m 755 ${S}/samples/rc.autofs ${D}${INIT_D_DIR}/autofs
    fi
}
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

INSANE_SKIP_${PN} = "dev-so"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "autofs.service"

