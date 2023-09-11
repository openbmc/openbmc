SUMMARY = "Provides a way to load and enumerate PKCS#11 modules"
DESCRIPTION = " Provides a standard configuration setup for installing PKCS#11 modules in such a way that they're discoverable. Also solves problems with coordinating the use of PKCS#11 by different components or libraries living in the same process."
HOMEPAGE = "https://p11-glue.github.io/p11-glue/p11-kit.html"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=02933887f609807fbb57aa4237d14a50"

inherit meson gettext pkgconfig gtk-doc bash-completion manpages

DEPENDS = "libtasn1 libtasn1-native libffi"

DEPENDS:append = "${@' glib-2.0' if d.getVar('GTKDOC_ENABLED') == 'True' else ''}"

SRC_URI = "git://github.com/p11-glue/p11-kit;branch=master;protocol=https \
           file://strerror-1.patch \
           file://strerror-2.patch"
SRCREV = "a8cce8bd8065bbf80bd47219f85f0cd9cf27dd0c"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native"
PACKAGECONFIG[trust-paths] = "-Dtrust_paths=/etc/ssl/certs/ca-certificates.crt,,,ca-certificates"

GTKDOC_MESON_OPTION = 'gtk_doc'

FILES:${PN} += " \
    ${libdir}/p11-kit-proxy.so \
    ${libdir}/pkcs11/*.so \
    ${libdir}/pkcs11/*.la \
    ${systemd_user_unitdir}/*"

# PN contains p11-kit-proxy.so, a symlink to a loadable module
INSANE_SKIP:${PN} = "dev-so"

BBCLASSEXTEND = "native nativesdk"
