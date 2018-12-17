SUMMARY = "pam-ssh-agent-auth"
DESCRIPTION = "A PAM module which permits authentication via ssh-agent."
HOMEPAGE = "http://sourceforge.net/projects/pamsshagentauth/"
SECTION = "libs"
LICENSE = "openssl & BSD"
LIC_FILES_CHKSUM = "file://LICENSE.OpenSSL;md5=8ab01146141ded59b75f8ba7811ed05a \
                    file://OPENSSH_LICENSE;md5=7ae09218173be1643c998a4b71027f9b \
"

SRC_URI = "http://sourceforge.net/projects/pamsshagentauth/files/pam_ssh_agent_auth/v${PV}/pam_ssh_agent_auth-${PV}.tar.bz2"
SRC_URI[md5sum] = "8dbe90ab3625e545036333e6f51ccf1d"
SRC_URI[sha256sum] = "3c53d358d6eaed1b211239df017c27c6f9970995d14102ae67bae16d4f47a763"

DEPENDS += "libpam openssl10"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "pam"

# This gets us ssh-agent, which we are almost certain to want.
#
RDEPENDS_${PN} += "openssh-misc"

# Kind of unfortunate to have underscores in the name.
#
S = "${WORKDIR}/pam_ssh_agent_auth-${PV}"

inherit autotools-brokensep

# Avoid autoreconf.  Override the --libexec oe_runconf specifies so that
# the module is put with the other pam modules.  Because it cannot, in general,
# do a runtime test, configure wants to use rpl_malloc() and rpl_realloc()
# instead of malloc() and realloc().  We set variables to tell it not to because
# these functions do not exist.
#
do_configure () {
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf --without-openssl-header-check  --libexecdir=${base_libdir}/security \
               ac_cv_func_malloc_0_nonnull=yes ac_cv_func_realloc_0_nonnull=yes
}

# Link with CC.  Configure cannot figure out the correct AR.
#
do_compile () {
    oe_runmake  LD="${CC}" AR="${AR}"
}

# This stuff is not any place looked at by default.
#
FILES_${PN} += "${base_libdir}/security/pam*"
FILES_${PN}-dbg += "${base_libdir}/security/.debug"
