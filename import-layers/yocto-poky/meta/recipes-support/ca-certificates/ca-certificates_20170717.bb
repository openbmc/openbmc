SUMMARY = "Common CA certificates"
DESCRIPTION = "This package includes PEM files of CA certificates to allow \
SSL-based applications to check for the authenticity of SSL connections. \
This derived from Debian's CA Certificates."
HOMEPAGE = "http://packages.debian.org/sid/ca-certificates"
SECTION = "misc"
LICENSE = "GPL-2.0+ & MPL-2.0"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=e7358b9541ccf3029e9705ed8de57968"

# This is needed to ensure we can run the postinst at image creation time
DEPENDS = ""
DEPENDS_class-native = "openssl-native"
DEPENDS_class-nativesdk = "openssl-native"
# Need c_rehash from openssl and run-parts from debianutils
PACKAGE_WRITE_DEPS += "openssl-native debianutils-native"

SRCREV = "34b8e19e541b8af4076616b2e170c7a70cdaded0"

SRC_URI = "git://salsa.debian.org/debian/ca-certificates.git;protocol=https \
           file://0002-update-ca-certificates-use-SYSROOT.patch \
           file://0001-update-ca-certificates-don-t-use-Debianisms-in-run-p.patch \
           file://update-ca-certificates-support-Toybox.patch \
           file://default-sysroot.patch \
           file://sbindir.patch \
           file://0003-update-ca-certificates-use-relative-symlinks-from-ET.patch \
           "

S = "${WORKDIR}/git"
SYSROOT_DIRS_class-native += "${sysconfdir} ${datadir}/ca-certificates"

inherit allarch

EXTRA_OEMAKE = "\
    'CERTSDIR=${datadir}/ca-certificates' \
    'SBINDIR=${sbindir}' \
"

do_compile_prepend() {
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
            sed 's,^${D}${datadir}/ca-certificates/,,'
    } >${D}${sysconfdir}/ca-certificates.conf
}

do_install_append_class-target () {
    sed -i -e 's,/etc/,${sysconfdir}/,' \
           -e 's,/usr/share/,${datadir}/,' \
           -e 's,/usr/local,${prefix}/local,' \
        ${D}${sbindir}/update-ca-certificates \
        ${D}${mandir}/man8/update-ca-certificates.8
}

pkg_postinst_${PN}_class-target () {
    SYSROOT="$D" $D${sbindir}/update-ca-certificates
}

CONFFILES_${PN} += "${sysconfdir}/ca-certificates.conf"

# Rather than make a postinst script that works for both target and nativesdk,
# we just run update-ca-certificate from do_install() for nativesdk.
CONFFILES_${PN}_append_class-nativesdk = " ${sysconfdir}/ssl/certs/ca-certificates.crt"
do_install_append_class-nativesdk () {
    SYSROOT="${D}${SDKPATHNATIVE}" ${D}${sbindir}/update-ca-certificates
}

do_install_append_class-native () {
    SYSROOT="${D}${base_prefix}" ${D}${sbindir}/update-ca-certificates
}

RDEPENDS_${PN} += "openssl"

BBCLASSEXTEND = "native nativesdk"
