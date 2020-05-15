SUMMARY = "Linux security scanner"
DESCRIPTION = "Buck-Security is a security scanner for Debian and Ubuntu Linux. It runs a couple of important checks and helps you to harden your Linux \
system. This enables you to quickly overview the security status of your Linux system."
SECTION = "security"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "http://sourceforge.net/projects/buck-security/files/buck-security/buck-security_${PV}/${BPN}_${PV}.tar.gz"

SRC_URI[md5sum] = "611a3e9bb7ed8a8270aa15216c321c53"
SRC_URI[sha256sum] = "c533c6631ec3554dd8d39d2d1c3ed44badbbf50810ebb75469c74639fa294b01"

S = "${WORKDIR}/${BPN}_${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}/buck
    cp -r ${S}/* ${D}${bindir}/buck
    cp -r ${S}/buck-security ${D}${bindir}
    sed -i 's!use lib "checks"!use lib File::Spec->catfile(dirname(File::Spec->rel2abs(__FILE__)), "buck/checks")!' ${D}${bindir}/buck-security
    sed -i 's!use lib "checks/lib"!use lib File::Spec->catfile(dirname(File::Spec->rel2abs(__FILE__)), "buck/checks/lib")!' ${D}${bindir}/buck-security
    sed -i 's!use lib "lib"!use lib File::Spec->catfile(dirname(File::Spec->rel2abs(__FILE__)), "buck/lib")!' ${D}${bindir}/buck-security
    sed -i 's!my $buck_root = "."!my $buck_root = File::Spec->catfile(dirname(File::Spec->rel2abs(__FILE__)), "buck")!' ${D}${bindir}/buck-security

}

FILES_${PN} = "${bindir}/*"

RDEPENDS_${PN} = "coreutils gnupg net-tools perl perl-module-data-dumper \
                  perl-module-file-basename perl-module-file-spec perl-module-getopt-long \
                  perl-module-lib perl-module-posix perl-module-term-ansicolor \
                  perl-module-time-localtime pinentry perl-module-pod-usage \
                  perl-module-pod-text perl-module-file-glob \
                 "

RDEPENDS_${PN}_class-native = "coreutils net-tools perl perl-module-data-dumper \
                               perl-module-file-basename perl-module-file-spec perl-module-getopt-long \
                               perl-module-lib perl-module-posix perl-module-term-ansicolor \
                               perl-module-time-localtime perl-module-file-glob\
                              "


BBCLASSEXTEND = "native"
