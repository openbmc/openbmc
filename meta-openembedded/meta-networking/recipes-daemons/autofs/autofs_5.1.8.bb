SUMMARY = "Kernel based automounter for linux"
SECTION = "utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ee9324a6f564bb2376b63878ac396798"

DEPENDS += "libtirpc flex-native bison-native e2fsprogs openssl libxml2 util-linux cyrus-sasl libnsl2"

CFLAGS += "-I${STAGING_INCDIR}/tirpc"

inherit autotools-brokensep systemd update-rc.d pkgconfig

SRC_URI = "${KERNELORG_MIRROR}/linux/daemons/autofs/v5/autofs-${PV}.tar.gz \
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
           file://0001-Define-__SWORD_TYPE-if-undefined.patch \
           file://mount_conflict.patch \
           file://0001-autofs-5.1.8-add-autofs_strerror_r-helper-for-musl.patch \
           file://0002-autofs-5.1.8-handle-innetgr-not-present-in-musl.patch \
           file://0001-include-libgen.h-for-basename.patch \
           "
SRC_URI[sha256sum] = "0bd401c56f0eb1ca6251344c3a3d70bface3eccf9c67117cd184422c4cace30c"

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

do_configure:prepend () {
    if [ ! -e ${S}/acinclude.m4 ]; then
        cp ${S}/aclocal.m4 ${S}/acinclude.m4
    fi
    cp ${STAGING_DATADIR_NATIVE}/aclocal/pkg.m4 .
}

do_install:append () {
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

# all the libraries are unversioned, so don't pack it on PN-dev
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
# Some symlinks are created in plugins dir e.g.
# mount_nfs4.so -> mount_nfs.so
INSANE_SKIP:${PN} = "dev-so"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "autofs.service"
