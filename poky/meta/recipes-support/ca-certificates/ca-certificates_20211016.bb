SUMMARY = "Common CA certificates"
DESCRIPTION = "This package includes PEM files of CA certificates to allow \
SSL-based applications to check for the authenticity of SSL connections. \
This derived from Debian's CA Certificates."
HOMEPAGE = "http://packages.debian.org/sid/ca-certificates"
SECTION = "misc"
LICENSE = "GPL-2.0-or-later & MPL-2.0"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=ae5b36b514e3f12ce1aa8e2ee67f3d7e"

# This is needed to ensure we can run the postinst at image creation time
DEPENDS = ""
DEPENDS:class-native = "openssl-native"
DEPENDS:class-nativesdk = "openssl-native"
# Need rehash from openssl and run-parts from debianutils
PACKAGE_WRITE_DEPS += "openssl-native debianutils-native"

SRCREV = "07de54fdcc5806bde549e1edf60738c6bccf50e8"

SRC_URI = "git://salsa.debian.org/debian/ca-certificates.git;protocol=https;branch=master \
           file://0002-update-ca-certificates-use-SYSROOT.patch \
           file://0001-update-ca-certificates-don-t-use-Debianisms-in-run-p.patch \
           file://default-sysroot.patch \
           file://0003-update-ca-certificates-use-relative-symlinks-from-ET.patch \
           file://0001-Revert-mozilla-certdata2pem.py-print-a-warning-for-e.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+)"

S = "${WORKDIR}/git"

inherit allarch

EXTRA_OEMAKE = "\
    'CERTSDIR=${datadir}/ca-certificates' \
    'SBINDIR=${sbindir}' \
"

do_compile:prepend() {
    oe_runmake clean
}

do_install () {
    install -d ${D}${datadir}/ca-certificates \
               ${D}${sysconfdir}/ssl/certs \
               ${D}${sysconfdir}/ca-certificates/update.d
    oe_runmake 'DESTDIR=${D}' install

    install -d ${D}${mandir}/man8
    install -m 0644 sbin/update-ca-certificates.8 ${D}${mandir}/man8/

    install -d ${D}${sysconfdir}
    {
        echo "# Lines starting with # will be ignored"
        echo "# Lines starting with ! will remove certificate on next update"
        echo "#"
        find ${D}${datadir}/ca-certificates -type f -name '*.crt' | \
            sed 's,^${D}${datadir}/ca-certificates/,,' | sort
    } >${D}${sysconfdir}/ca-certificates.conf
}

do_install:append:class-target () {
    sed -i -e 's,/etc/,${sysconfdir}/,' \
           -e 's,/usr/share/,${datadir}/,' \
           -e 's,/usr/local,${prefix}/local,' \
        ${D}${sbindir}/update-ca-certificates \
        ${D}${mandir}/man8/update-ca-certificates.8
}

pkg_postinst:${PN}:class-target () {
    SYSROOT="$D" $D${sbindir}/update-ca-certificates
}

CONFFILES:${PN} += "${sysconfdir}/ca-certificates.conf"

# Rather than make a postinst script that works for both target and nativesdk,
# we just run update-ca-certificate from do_install() for nativesdk.
CONFFILES:${PN}:append:class-nativesdk = " ${sysconfdir}/ssl/certs/ca-certificates.crt"
do_install:append:class-nativesdk () {
    SYSROOT="${D}${SDKPATHNATIVE}" ${D}${sbindir}/update-ca-certificates
}

do_install:append:class-native () {
    SYSROOT="${D}${base_prefix}" ${D}${sbindir}/update-ca-certificates
}

RDEPENDS:${PN}:append:class-target = " openssl-bin openssl"
RDEPENDS:${PN}:append:class-native = " openssl-native"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-openssl-bin nativesdk-openssl"

BBCLASSEXTEND = "native nativesdk"
