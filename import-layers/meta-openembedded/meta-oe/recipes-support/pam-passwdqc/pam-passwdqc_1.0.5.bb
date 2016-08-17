SUMMARY = "Pluggable password quality-control module."
DESCRIPTION = "pam_passwdqc is a simple password strength checking module for \
PAM-aware password changing programs, such as passwd(1). In addition \
to checking regular passwords, it offers support for passphrases and \
can provide randomly generated passwords. All features are optional \
and can be (re-)configured without rebuilding."

HOMEPAGE = "http://www.openwall.com/passwdqc/"
SECTION = "System Environment/Base"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e284d013ef08e66d4737f446c5890550"

SRC_URI = "http://www.openwall.com/pam/modules/pam_passwdqc/pam_passwdqc-1.0.5.tar.gz \
           file://1000patch-219201.patch \
           file://7000Makefile-fix-CC.patch \
          "
SRC_URI[md5sum] = "cd9c014f736158b1a60384a8e2bdc28a"
SRC_URI[sha256sum] = "32528ddf7d8219c788b6e7702361611ff16c6340b6dc0f418ff164aadc4a4a88"


S = "${WORKDIR}/pam_passwdqc-${PV}"

DEPENDS = "libpam"

EXTRA_OEMAKE = "CFLAGS="${CFLAGS} -Wall -fPIC -DHAVE_SHADOW" \
                SECUREDIR=${base_libdir}/security"

do_install() {
	oe_runmake install DESTDIR=${D}
}

FILES_${PN} += "${base_libdir}/security/pam_passwdqc.so"
FILES_${PN}-dbg += "${base_libdir}/security/.debug"

