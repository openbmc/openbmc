SUMMARY = "PAM searchless LDAP authentication module"
HOMEPAGE = "https://github.com/rmbreak/pam_ldapdb"
BUGTRACKER = "https://github.com/rmbreak/pam_ldapdb/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41ab94182d94be9bb35e2a8b933f1e7d"

DEPENDS = "libpam openldap"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRCREV = "3e026863cad1fd45c760ee1bc93ef4f0606cc852"
SRC_URI = "git://github.com/rmbreak/pam_ldapdb;branch=master;protocol=https"

S = "${WORKDIR}/git"

do_install () {
    oe_runmake install DESTDIR=${D} PAMDIR=${base_libdir}/security
}

FILES:${PN} += "${base_libdir}/security/pam_ldapdb.so"
