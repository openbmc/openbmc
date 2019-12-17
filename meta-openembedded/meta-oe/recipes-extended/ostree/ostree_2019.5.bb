SUMMARY = "Versioned Operating System Repository."
DESCRIPTION = "libostree is both a shared library and suite of command line \
tools that combines a \"git-like\" model for committing and downloading \
bootable filesystem trees, along with a layer for deploying them and managing \
the bootloader configuration."
HOMEPAGE = "https://ostree.readthedocs.io"
LICENSE = "LGPLv2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = " \
    glib-2.0 \
    gpgme \
    e2fsprogs \
    libcap \
    zlib \
    xz \
    bison-native \
"

# The Yocto mirror has an old export of ostree:
# http://downloads.yoctoproject.org/mirror/sources/git2_github.com.ostreedev.ostree.tar.gz
PREMIRRORS = ""

SRC_URI = " \
    gitsm://github.com/ostreedev/ostree \
    file://0001-macros-Add-TEMP_FAILURE_RETRY-for-musl.patch \
    file://run-ptest \
    file://0001-tests-core-Fallback-to-en_US.UTF-8-locale.patch \
    file://0001-tests-Handle-EPIPE-failures-when-head-terminates.patch \
    file://0002-tests-core-Assume-C.UTF-8-if-locale-isn-t-found.patch \
    file://0003-tests-Avoid-musl-failure-with-cp-a.patch \
    file://0001-build-create-tests-directory-for-split-builds.patch \
"
SRCREV = "980ca07b03b3aa7e0012729dd6c84b0878775d93"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+)"

S = "${WORKDIR}/git"

inherit autotools bash-completion gobject-introspection gtk-doc pkgconfig ptest-gnome systemd

# Package configuration - match ostree defaults, but without rofiles-fuse
# otherwise we introduce a dependendency on meta-filesystems
PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd libmount', '', d)} \
    soup \
"

# We include soup because ostree can't (currently) be built without
# soup or curl - https://github.com/ostreedev/ostree/issues/1897
PACKAGECONFIG_class-native ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux smack', d)} \
    soup \
"

PACKAGECONFIG[avahi] = "--with-avahi, --without-avahi, avahi"
PACKAGECONFIG[curl] = "--with-curl, --without-curl, curl"
PACKAGECONFIG[dracut] = "--with-dracut, --without-dracut"
PACKAGECONFIG[gnutls] = "--with-crypto=gnutls, , gnutls"
PACKAGECONFIG[libarchive] = "--with-libarchive, --without-libarchive, libarchive"
PACKAGECONFIG[libmount] = "--with-libmount, --without-libmount, util-linux"
PACKAGECONFIG[man] = "--enable-man, --disable-man, libxslt-native docbook-xsl-stylesheets-native"
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

EXTRA_OECONF_class-native = " \
    --with-builtin-grub2-mkconfig \
    --enable-wrpseudo-compat \
    --disable-otmpfile \
"

# Path to ${prefix}/lib/ostree/ostree-grub-generator is hardcoded on the
# do_configure stage so we do depend on it
SYSROOT_DIR = "${STAGING_DIR_TARGET}"
SYSROOT_DIR_class-native = "${STAGING_DIR_NATIVE}"
do_configure[vardeps] += "SYSROOT_DIR"

do_configure_prepend() {
    # this reflects what autogen.sh does, but the OE wrappers for autoreconf
    # allow it to work without the other gyrations which exist there
    cp ${S}/libglnx/Makefile-libglnx.am ${S}/libglnx/Makefile-libglnx.am.inc
    cp ${S}/bsdiff/Makefile-bsdiff.am ${S}/bsdiff/Makefile-bsdiff.am.inc
}

do_install_append_class-native() {
    create_wrapper ${D}${bindir}/ostree OSTREE_GRUB2_EXEC="${STAGING_LIBDIR_NATIVE}/ostree/ostree-grub-generator"
}

PACKAGES += " \
    ${PN}-dracut \
    ${PN}-grub \
    ${PN}-mkinitcpio \
    ${PN}-switchroot \
    ${PN}-trivial-httpd \
"

FILES_${PN} = " \
    ${bindir}/ostree \
    ${bindir}/rofiles-fuse \
    ${datadir}/${BPN} \
    ${datadir}/gir-1.0 \
    ${libdir}/${BPN}/ostree-remount \
    ${libdir}/girepository-1.0 \
    ${libdir}/lib*${SOLIBS} \
    ${libdir}/tmpfiles.d/ostree-tmpfiles.conf \
    ${sysconfdir}/ostree/remotes.d \
    ${systemd_unitdir}/system-generators/ostree-system-generator \
    ${systemd_unitdir}/system/ostree-finalize-staged.path \
    ${systemd_unitdir}/system/ostree-finalize-staged.service \
    ${systemd_unitdir}/system/ostree-remount.service \
"
FILES_${PN}-dracut = " \
    ${sysconfdir}/dracut.conf.d \
    ${libdir}/dracut \
"
FILES_${PN}-grub = " \
    ${sysconfdir}/grub.d \
    ${libexecdir}/libostree/grub2-15_ostree \
"
FILES_${PN}-mkinitcpio = " \
    ${sysconfdir}/ostree-mkinitcpio.conf \
    ${libdir}/initcpio \
"
FILES_${PN}-switchroot = " \
    ${libdir}/ostree/ostree-prepare-root \
    ${systemd_unitdir}/system/ostree-prepare-root.service \
"
FILES_${PN}-trivial-httpd = " \
    ${libexecdir}/libostree/ostree-trivial-httpd \
"

RDEPENDS_${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'trivial-httpd-cmdline', '${PN}-trivial-httpd', '', d)} \
"
RDEPENDS_${PN}-dracut = "bash"
RDEPENDS_${PN}-mkinitcpio = "bash"
RDEPENDS_${PN}_class-target = " \
    gnupg \
    ${PN}-switchroot \
"
RDEPENDS_${PN}-ptest += " \
    attr \
    bash \
    coreutils \
    cpio \
    diffutils \
    findutils \
    grep \
    python3-core \
    python3-multiprocessing \
    python3-pyyaml \
    ${PN}-trivial-httpd \
"
RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-utils glibc-localedata-en-us"

RRECOMMENDS_${PN} += "kernel-module-overlay"
RRECOMMENDS_${PN}-ptest += "strace"

SYSTEMD_SERVICE_${PN} = "ostree-remount.service ostree-finalize-staged.path"
SYSTEMD_SERVICE_${PN}-switchroot = "ostree-prepare-root.service"

BBCLASSEXTEND = "native"

python __anonymous() {
    if not bb.data.inherits_class('native', d) and bb.utils.contains('PTEST_ENABLED', '1', 'True', 'False', d):
        if not bb.utils.contains_any('BBFILE_COLLECTIONS', 'meta-python', 'True', 'False', d):
            raise bb.parse.SkipRecipe('ptest requires meta-python to be present.')
        elif not bb.utils.contains_any('PACKAGECONFIG', 'soup curl', 'True', 'False', d):
            raise bb.parse.SkipRecipe('ptest requires soup enabled in PACKAGECONFIG.')
        elif not oe.utils.any_distro_features(d, "xattr"):
            raise bb.parse.SkipRecipe('ptest requires xattr enabled in DISTRO_FEATURES.')
}
