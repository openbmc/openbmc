SUMMARY = "PAM searchless LDAP authentication module"
HOMEPAGE = "https://github.com/rmbreak/pam_ldapdb"
BUGTRACKER = "https://github.com/rmbreak/pam_ldapdb/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41ab94182d94be9bb35e2a8b933f1e7d"

DEPENDS = "libpam openldap"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRCREV = "84d7b260f1ae6857ae36e014c9a5968e8aa1cbe8"
SRC_URI = "git://github.com/rmbreak/pam_ldapdb;branch=master;protocol=https \
           file://0001-include-stdexcept-for-std-invalid_argument.patch \
"

S = "${WORKDIR}/git"

do_install () {
    oe_runmake install DESTDIR=${D} PAMDIR=${base_libdir}/security
}

FILES_${PN} += "${base_libdir}/security/pam_ldapdb.so"
