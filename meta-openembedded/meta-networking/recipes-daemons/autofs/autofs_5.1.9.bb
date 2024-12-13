SUMMARY = "Kernel based automounter for linux"
SECTION = "utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ee9324a6f564bb2376b63878ac396798"

DEPENDS += "libtirpc flex-native bison-native e2fsprogs openssl util-linux libnsl2"

CFLAGS += "-I${STAGING_INCDIR}/tirpc"

inherit autotools-brokensep systemd update-rc.d pkgconfig

SRC_URI = "${KERNELORG_MIRROR}/linux/daemons/autofs/v5/autofs-${PV}.tar.gz \
           file://0001-no-bash.patch \
           file://0002-using-pkg-config-to-detect-krb5.patch \
           file://0003-force-STRIP-to-emtpy.patch \
           file://0004-autofs.init.in-remove-bashism.patch \
           file://0005-fix-the-YACC-rule-to-fix-a-building-failure.patch \
           file://0006-Do-not-hardcode-path-for-pkg.m4.patch \
           file://0007-Avoid-conflicts-between-sys-mount.h-and-linux-mount..patch \
           file://0008-include-libgen.h-for-basename.patch \
           file://0009-hash.h-include-sys-reg.h-instead-of-bits-reg.h.patch \
           file://0010-autofs-5.1.9-Fix-incompatible-function-pointer-types.patch \
           "
SRC_URI[sha256sum] = "46c30b763ef896f4c4a6df6d62aaaef7afc410e0b7f50d52dbfc6cf728cacd4f"

UPSTREAM_CHECK_URI = "${KERNELORG_MIRROR}/linux/daemons/autofs/v5/"

INITSCRIPT_NAME = "autofs"
INITSCRIPT_PARAMS = "defaults"

PACKAGECONFIG[systemd] = "--with-systemd=${systemd_unitdir}/system,--without-systemd,systemd"
PACKAGECONFIG[openldap] = "--with-openldap=yes,--with-openldap=no,libxml2 openldap"
PACKAGECONFIG[sasl] = "--with-sasl=yes,--with-sasl=no,cyrus-sasl krb5"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

EXTRA_OEMAKE = "DONTSTRIP=1"
EXTRA_OECONF += "--disable-mount-locking \
                --enable-ignore-busy \
                --with-confdir=${sysconfdir}/default \
                --with-fifodir=/run \
                --with-flagdir=/run \
                --with-libtirpc \
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
