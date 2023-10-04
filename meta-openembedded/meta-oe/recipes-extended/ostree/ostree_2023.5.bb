SUMMARY = "Versioned Operating System Repository."
DESCRIPTION = "libostree is both a shared library and suite of command line \
tools that combines a \"git-like\" model for committing and downloading \
bootable filesystem trees, along with a layer for deploying them and managing \
the bootloader configuration."
HOMEPAGE = "https://ostree.readthedocs.io"
LICENSE = "LGPL-2.1-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = " \
    glib-2.0-native \
    glib-2.0 \
    e2fsprogs \
    libcap \
    zlib \
    xz \
    bison-native \
"

GITHUB_BASE_URI = "https://github.com/ostreedev/ostree/releases"
SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/libostree-${PV}.tar.xz \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "bc593afb31fe1ac3d50419f917fafe321a0a3561d7bb2ba498a83740fe3adb14"

S = "${WORKDIR}/libostree-${PV}"

inherit autotools bash-completion gobject-introspection github-releases gtk-doc manpages pkgconfig ptest-gnome systemd

COMPATIBLE_HOST:riscv32 = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'null', 'riscv32', d)}"

UNKNOWN_CONFIGURE_OPT_IGNORE = "--disable-introspection --enable-introspection"

# Workaround compile failure:
# |../git/src/libotutil/zbase32.c:37:1: error: function returns an aggregate [-Werror=aggregate-return]
# so remove -Og and use -O2 as workaround
DEBUG_OPTIMIZATION:remove = "-Og"
DEBUG_OPTIMIZATION:append = " -O2"
BUILD_OPTIMIZATION:remove = "-Og"
BUILD_OPTIMIZATION:append = " -O2"

# Package configuration - match ostree defaults, but without rofiles-fuse
# otherwise we introduce a dependendency on meta-filesystems and swap
# soup for curl to avoid bringing in deprecated libsoup2 (though
# to run ptest requires that you have soup2 or soup3).
PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd libmount', '', d)} \
    glib \
    gpgme \
    curl \
"

# We include curl because ostree can't (currently) be built without
# soup or curl - https://github.com/ostreedev/ostree/issues/1897
PACKAGECONFIG:class-native ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    builtin-grub2-mkconfig \
    gpgme \
    curl \
"

PACKAGECONFIG:class-nativesdk ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    builtin-grub2-mkconfig \
    gpgme \
    curl \
"

PACKAGECONFIG[avahi] = "--with-avahi, --without-avahi, avahi"
PACKAGECONFIG[builtin-grub2-mkconfig] = "--with-builtin-grub2-mkconfig, --without-builtin-grub2-mkconfig"
PACKAGECONFIG[curl] = "--with-curl, --without-curl, curl"
PACKAGECONFIG[dracut] = "--with-dracut, --without-dracut"
PACKAGECONFIG[ed25519-libsodium] = "--with-ed25519-libsodium, --without-ed25519-libsodium, libsodium"
PACKAGECONFIG[gjs] = "ac_cv_path_GJS=${bindir}/gjs"
PACKAGECONFIG[glib] = "--with-crypto=glib, , , , , gnutls openssl"
PACKAGECONFIG[gnutls] = "--with-crypto=gnutls, , gnutls, , , glib openssl"
PACKAGECONFIG[gpgme] = "--with-gpgme, --without-gpgme, gpgme"
PACKAGECONFIG[libarchive] = "--with-libarchive, --without-libarchive, libarchive"
PACKAGECONFIG[libmount] = "--with-libmount, --without-libmount, util-linux"
PACKAGECONFIG[manpages] = "--enable-man, --disable-man, libxslt-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[mkinitcpio] = "--with-mkinitcpio, --without-mkinitcpio"
PACKAGECONFIG[no-http2] = "--disable-http2, --enable-http2"
PACKAGECONFIG[openssl] = "--with-crypto=openssl, , openssl, , , glib gnutls"
PACKAGECONFIG[rofiles-fuse] = "--enable-rofiles-fuse, --disable-rofiles-fuse, fuse3"
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux, bubblewrap"
PACKAGECONFIG[smack] = "--with-smack, --without-smack, smack"
PACKAGECONFIG[soup2] = "--with-soup, --without-soup, libsoup-2.4, , , soup3"
PACKAGECONFIG[soup3] = "--with-soup3, --without-soup3, libsoup, , , soup2"
PACKAGECONFIG[static] = ""
PACKAGECONFIG[systemd] = "--with-libsystemd --with-systemdsystemunitdir=${systemd_system_unitdir}, --without-libsystemd, systemd"
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
    ${systemd_system_unitdir} \
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
    ${systemd_system_unitdir}/ostree-prepare-root.service \
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
#   xattr in DISTRO_FEATURES (default)
#   static ostree-prepare-root
#   ostree-trivial-httpd (requires soup - note soup and curl can coexist)
#   overlayfs in your kernel
#   busybox built statically
#   C.UTF-8 locale available (default)
#   Sufficient disk space/RAM (e.g. core-image-sato-sdk)
#
# Something like this in your local.conf:
#
# PACKAGECONFIG:append:pn-ostree = " static soup3"
# KERNEL_EXTRA_FEATURES:append = " features/overlayfs/overlayfs.scc"
# TARGET_CFLAGS:append:pn-busybox = " -static"
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
