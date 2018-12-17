SUMMARY = "PAM searchless LDAP authentication module"
HOMEPAGE = "https://github.com/rmbreak/pam_ldapdb"
BUGTRACKER = "https://github.com/rmbreak/pam_ldapdb/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41ab94182d94be9bb35e2a8b933f1e7d"

DEPENDS = "libpam openldap"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRC_URI = "https://github.com/rmbreak/pam_ldapdb/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "2dd4f1370fcfe995ee0ad09611109b87"
SRC_URI[sha256sum] = "8ed92b36523556bb5d9bf3eb33a1035e46041d4be767c8d62136930c0ca0e45b"

S = "${WORKDIR}/pam_ldapdb-${PV}"

do_install () {
    oe_runmake install DESTDIR=${D} PAMDIR=${base_libdir}/security
}

FILES_${PN} += "${base_libdir}/security/pam_ldapdb.so"
