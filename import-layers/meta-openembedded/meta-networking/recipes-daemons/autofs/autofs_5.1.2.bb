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
           file://libtirpc.patch \
           file://autofs-5.0.7-do-not-check-for-modprobe.patch \
           file://fix_disable_ldap.patch \
           file://autofs-5.0.7-fix-lib-deps.patch \
           file://add-the-needed-stdarg.h.patch \
           file://using-pkg-config-to-detect-libxml-2.0-and-krb5.patch \
           file://force-STRIP-to-emtpy.patch \
           file://remove-bashism.patch \
           file://fix-the-YACC-rule-to-fix-a-building-failure.patch \
           file://0001-Define-__SWORD_TYPE-and-_PATH_NSSWITCH_CONF.patch \
           file://0002-Replace-__S_IEXEC-with-S_IEXEC.patch \
           file://autofs-5.1.2-libtirpc-as-need.patch \
           file://pkgconfig-libnsl.patch \
           file://0001-modules-lookup_multi.c-Replace-__S_IEXEC-with-S_IEXE.patch \
           "
SRC_URI[md5sum] = "28cf88f99eff553a8500659ba5d45a76"
SRC_URI[sha256sum] = "0d57e4138c2ec8058ca92164d035546f68ce4af93acb893369993d67c7056a10"

INITSCRIPT_NAME = "autofs"
INITSCRIPT_PARAMS = "defaults"

# FIXME: modules/Makefile has crappy rules that don't obey LDFLAGS
#CFLAGS += "${LDFLAGS}"

PACKAGECONFIG[systemd] = "--with-systemd=${systemd_unitdir}/system,--without-systemd,systemd"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

EXTRA_OEMAKE = "DONTSTRIP=1"
EXTRA_OECONF += "--disable-mount-locking \
                --enable-ignore-busy --with-openldap=no \
                --with-sasl=no --with-libtirpc \
                --with-path=${STAGING_BINDIR_NATIVE} \
"
CACHED_CONFIGUREVARS = "ac_cv_path_RANLIB=${RANLIB} \
                        ac_cv_path_RPCGEN=rpcgen \
"

do_configure_prepend () {
    sed -e "s:filagdir:flagdir:" -i ${S}/configure.in
    if [ ! -e ${S}/acinclude.m4 ]; then
        cp ${S}/aclocal.m4 ${S}/acinclude.m4
    fi
}

do_install_append () {
    if [ -d ${D}/run ]; then
        rmdir ${D}/run
    fi
    if [ -d ${D}${localstatedir}/run ]; then
        rmdir ${D}${localstatedir}/run
    fi
}
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

INSANE_SKIP_${PN} = "dev-so"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "autofs.service"

