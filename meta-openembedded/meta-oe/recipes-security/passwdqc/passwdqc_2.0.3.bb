SUMMARY = "A password/passphrase strength checking and enforcement toolset"
DESCRIPTION = "\
passwdqc is a password/passphrase strength checking and policy enforcement \
toolset, including an optional PAM module (pam_passwdqc), command-line \
programs (pwqcheck and pwqgen), and a library (libpasswdqc). \
pam_passwdqc is normally invoked on password changes by programs such as \
passwd(1).  It is capable of checking password or passphrase strength, \
enforcing a policy, and offering randomly-generated passphrases, with \
all of these features being optional and easily (re-)configurable. \
\
pwqcheck and pwqgen are standalone password/passphrase strength checking \
and random passphrase generator programs, respectively, which are usable \
from scripts. \
\
libpasswdqc is the underlying library, which may also be used from \
third-party programs. \
"

HOMEPAGE = "http://www.openwall.com/passwdqc"
SECTION = "System Environment/Base"

DEPENDS += "libpam"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

LICENSE = "BSD-1-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ac99c8678577a1c2f9f04cccee411d5d"

SRC_URI = "http://www.openwall.com/${BPN}/${BP}.tar.gz \
           file://makefile-add-ldflags.patch \
          "
SRC_URI[sha256sum] = "53b0f4bc49369f06195e9e13abb6cff352d5acb79e861004ec95973896488cf4"

# explicitly define LINUX_PAM in case DISTRO_FEATURES no pam
# this package's pam_passwdqc.so needs pam
CFLAGS:append = " -Wall -fPIC -DHAVE_SHADOW -DLINUX_PAM"

# -e is no longer default setting in bitbake.conf
EXTRA_OEMAKE = "-e"

do_compile() {
    # make sure sub make use environment to override variables in Makefile
    # Linux)    $(MAKE), there is a tab between
    sed -i -e 's/Linux)	$(MAKE) CFLAGS_lib/Linux)	$(MAKE) -e CFLAGS_lib/' ${S}/Makefile

    # LD_lib and LD must be CC because of Makefile
    oe_runmake LD="${CC}"
}

do_install() {
    oe_runmake install DESTDIR=${D} SHARED_LIBDIR=${base_libdir} \
           DEVEL_LIBDIR=${libdir} SECUREDIR=${base_libdir}/security \
           INSTALL="install -p"
}

PROVIDES += "pam-${BPN}"
PACKAGES =+ "lib${BPN} pam-${BPN}"

FILES:lib${BPN} = "${base_libdir}/libpasswdqc.so.1"
FILES:pam-${BPN} = "${base_libdir}/security/pam_passwdqc.so"
FILES:${PN}-dbg += "${base_libdir}/security/.debug"

RDEPENDS:${PN} = "lib${BPN} pam-${BPN}"
RDEPENDS:pam-${BPN} = "lib${BPN}"
