SUMMARY = "Provides a way to load and enumerate PKCS#11 modules"
DESCRIPTION = " Provides a standard configuration setup for installing PKCS#11 modules in such a way that they're discoverable. Also solves problems with coordinating the use of PKCS#11 by different components or libraries living in the same process."
HOMEPAGE = "https://p11-glue.github.io/p11-glue/p11-kit.html"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=02933887f609807fbb57aa4237d14a50"

inherit meson gettext pkgconfig gtk-doc bash-completion manpages lib_package

DEPENDS = "libffi"

DEPENDS:append = "${@' glib-2.0' if d.getVar('GTKDOC_ENABLED') == 'True' else ''}"

SRC_URI = "gitsm://github.com/p11-glue/p11-kit;branch=master;protocol=https;tag=${PV}"
SRCREV = "8e6e4e6d64d9fe91c62b0052c105b2b72d4c24ef"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} trust"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native"
PACKAGECONFIG[trust] = "-Dtrust_module=enabled,-Dtrust_module=disabled,libtasn1-native libtasn1"
PACKAGECONFIG[trust-paths] = "-Dtrust_paths=/etc/ssl/certs/ca-certificates.crt,,,ca-certificates"

EXTRA_OEMESON = "\
    -Dtest=false \
    -Dzsh_completion=disabled \
    -Dnls=${@'false' if d.getVar('USE_NLS') == 'no' else 'true'}"

GTKDOC_MESON_OPTION = 'gtk_doc'

PACKAGES =+ "${PN}-modules ${PN}-remote"

FILES:${PN}-bin += "${libexecdir}/p11-kit/trust-extract-compat"

FILES:${PN}-modules = "\
    ${datadir}/p11-kit/modules \
    ${libdir}/p11-kit-proxy.so \
    ${libdir}/pkcs11"

# p11-kit-proxy.so, a symlink to a loadable module
INSANE_SKIP:${PN}-modules = "dev-so"

FILES:${PN}-remote = "\
    ${libexecdir}/p11-kit/p11-kit-remote \
    ${libexecdir}/p11-kit/p11-kit-server \
    ${systemd_user_unitdir}"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2026-2100] = "fixed-version: fixed since 0.26.2"
