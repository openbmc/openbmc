SUMMARY = "Versioned Operating System Repository."
DESCRIPTION = "libostree is both a shared library and suite of command line \
tools that combines a \"git-like\" model for committing and downloading \
bootable filesystem trees, along with a layer for deploying them and managing \
the bootloader configuration."
HOMEPAGE = "https://ostree.readthedocs.io"
LICENSE = "LGPL-2.1-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = " \
    glib-2.0 \
    e2fsprogs \
    libcap \
    zlib \
    xz \
    bison-native \
"

SRC_URI = " \
    gitsm://github.com/ostreedev/ostree;branch=main;protocol=https \
    file://run-ptest \
"
SRCREV = "f1155c8d283c3c85d74d5e1050b0dcf8198f750a"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+)"

S = "${WORKDIR}/git"

inherit autotools bash-completion gobject-introspection gtk-doc manpages pkgconfig ptest-gnome systemd

# Workaround compile failure:
# |../git/src/libotutil/zbase32.c:37:1: error: function returns an aggregate [-Werror=aggregate-return]
# so remove -Og and use -O2 as workaround
DEBUG_OPTIMIZATION:remove = "-Og"
DEBUG_OPTIMIZATION:append = " -O2"
BUILD_OPTIMIZATION:remove = "-Og"
BUILD_OPTIMIZATION:append = " -O2"

# Package configuration - match ostree defaults, but without rofiles-fuse
# otherwise we introduce a dependendency on meta-filesystems
PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd libmount', '', d)} \
    glib \
    gpgme \
    soup \
"

# We include soup because ostree can't (currently) be built without
# soup or curl - https://github.com/ostreedev/ostree/issues/1897
PACKAGECONFIG:class-native ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    builtin-grub2-mkconfig \
    gpgme \
    soup \
"

PACKAGECONFIG:class-nativesdk ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    builtin-grub2-mkconfig \
    gpgme \
    soup \
"

PACKAGECONFIG[avahi] = "--with-avahi, --without-avahi, avahi"
PACKAGECONFIG[builtin-grub2-mkconfig] = "--with-builtin-grub2-mkconfig, --without-builtin-grub2-mkconfig"
PACKAGECONFIG[curl] = "--with-curl, --without-curl, curl"
PACKAGECONFIG[dracut] = "--with-dracut, --without-dracut"
PACKAGECONFIG[glib] = "--with-crypto=glib"
PACKAGECONFIG[gjs] = "ac_cv_path_GJS=${bindir}/gjs"
PACKAGECONFIG[gnutls] = "--with-crypto=gnutls, , gnutls"
PACKAGECONFIG[gpgme] = "--with-gpgme, --without-gpgme, gpgme"
PACKAGECONFIG[libarchive] = "--with-libarchive, --without-libarchive, libarchive"
PACKAGECONFIG[libmount] = "--with-libmount, --without-libmount, util-linux"
PACKAGECONFIG[manpages] = "--enable-man, --disable-man, libxslt-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[mkinitcpio] = "--with-mkinitcpio, --without-mkinitcpio"
PACKAGECONFIG[no-http2] = "--disable-http2, --enable-http2"
PACKAGECONFIG[openssl] = "--with-crypto=openssl, , openssl"
PACKAGECONFIG[rofiles-fuse] = "--enable-rofiles-fuse, --disable-rofiles-fuse, fuse"
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux"
PACKAGECONFIG[smack] = "--with-smack, --without-smack, smack"
PACKAGECONFIG[soup] = "--with-soup, --without-soup --disable-glibtest, libsoup-2.4"
PACKAGECONFIG[static] = ""
PACKAGECONFIG[systemd] = "--with-libsystemd --with-systemdsystemunitdir=${systemd_unitdir}/system, --without-libsystemd, systemd"
PACKAGECONFIG[trivial-httpd-cmdline] = "--enable-trivial-httpd-cmdline, --disable-trivial-httpd-cmdline"

EXTRA_OECONF = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', '--with-static-compiler=\'${CC} ${CFLAGS} ${CPPFLAGS} ${LDFLAGS}\'', '', d)} \
"

# Makefile-libostree.am overrides this to avoid a build problem with clang,
# but that fix breaks cross compilation and we don't need it
EXTRA_OEMAKE = " \
    INTROSPECTION_SCANNER_ENV= \
"

EXTRA_OECONF:class-native = " \
    --enable-wrpseudo-compat \
    --disable-otmpfile \
"

EXTRA_OECONF:class-nativesdk = " \
    --enable-wrpseudo-compat \
    --disable-otmpfile \
"

# Path to ${prefix}/lib/ostree/ostree-grub-generator is hardcoded on the
# do_configure stage so we do depend on it
SYSROOT_DIR = "${STAGING_DIR_TARGET}"
SYSROOT_DIR:class-native = "${STAGING_DIR_NATIVE}"
do_configure[vardeps] += "SYSROOT_DIR"

do_configure:prepend() {
    # this reflects what autogen.sh does, but the OE wrappers for autoreconf
    # allow it to work without the other gyrations which exist there
    cp ${S}/libglnx/Makefile-libglnx.am ${S}/libglnx/Makefile-libglnx.am.inc
    cp ${S}/bsdiff/Makefile-bsdiff.am ${S}/bsdiff/Makefile-bsdiff.am.inc
}

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/ostree OSTREE_GRUB2_EXEC="${STAGING_LIBDIR_NATIVE}/ostree/ostree-grub-generator"
}

do_install:append:class-nativesdk() {
    create_wrapper ${D}${bindir}/ostree OSTREE_GRUB2_EXEC="\$OECORE_NATIVE_SYSROOT/usr/lib/ostree/ostree-grub-generator"
}

PACKAGE_BEFORE_PN = " \
    ${PN}-dracut \
    ${PN}-grub \
    ${PN}-mkinitcpio \
    ${PN}-switchroot \
    ${PN}-trivial-httpd \
"

FILES:${PN} += " \
    ${nonarch_libdir}/${BPN} \
    ${nonarch_libdir}/tmpfiles.d \
    ${systemd_unitdir}/system \
    ${systemd_unitdir}/system-generators \
"
FILES:${PN}-dracut = " \
    ${sysconfdir}/dracut.conf.d \
    ${libdir}/dracut \
"
FILES:${PN}-grub = " \
    ${sysconfdir}/grub.d \
    ${libexecdir}/libostree/grub2-15_ostree \
"
FILES:${PN}-mkinitcpio = " \
    ${sysconfdir}/ostree-mkinitcpio.conf \
    ${libdir}/initcpio \
"
FILES:${PN}-switchroot = " \
    ${nonarch_libdir}/${BPN}/ostree-prepare-root \
    ${systemd_unitdir}/system/ostree-prepare-root.service \
"
FILES:${PN}-trivial-httpd = " \
    ${libexecdir}/libostree/ostree-trivial-httpd \
"

RDEPENDS:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'trivial-httpd-cmdline', '${PN}-trivial-httpd', '', d)} \
"
RDEPENDS:${PN}-dracut = "bash"
RDEPENDS:${PN}-mkinitcpio = "bash"
RDEPENDS:${PN}:class-target = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'gpgme', 'gnupg', '', d)} \
    ${PN}-switchroot \
"

#
# Note that to get ptest to pass you also need:
#
#   xattr in DISTRO_FEATURES
#   static ostree-prepare-root (PACKAGECONFIG:append:pn-ostree = " static")
#   meta-python in your layers
#   overlayfs in your kernel (KERNEL_EXTRA_FEATURES += "features/overlayfs/overlayfs.scc")
#   busybox built statically
#   /var/tmp as a real filesystem (not a tmpfs)
#   Sufficient disk space (IMAGE_ROOTFS_SIZE = "524288") and RAM (QB_MEM = "-m 1024")
#
RDEPENDS:${PN}-ptest += " \
    attr \
    bash \
    coreutils \
    cpio \
    diffutils \
    findutils \
    grep \
    python3-core \
    python3-multiprocessing \
    strace \
    tar \
    util-linux \
    xz \
    ${PN}-trivial-httpd \
    python3-pyyaml \
    ${@bb.utils.contains('PACKAGECONFIG', 'gjs', 'gjs', '', d)} \
"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils glibc-localedata-en-us"

RRECOMMENDS:${PN}:append:class-target = " kernel-module-overlay"

SYSTEMD_SERVICE:${PN} = "ostree-remount.service ostree-finalize-staged.path"
SYSTEMD_SERVICE:${PN}-switchroot = "ostree-prepare-root.service"

BBCLASSEXTEND = "native nativesdk"
