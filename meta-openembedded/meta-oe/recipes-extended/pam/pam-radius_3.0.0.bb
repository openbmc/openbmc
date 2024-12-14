SUMMARY = "PAM module for RADIUS authentication"
DESCRIPTION = "This allows any PAM-capable machine to become a RADIUS client for \
authentication and accounting requests. You will need a RADIUS server to perform \
the actual authentication."
HOMEPAGE = "http://freeradius.org/pam_radius_auth/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbbd794e2a0a289b9dfcc9f513d1996e"

SRC_URI = "git://github.com/FreeRADIUS/pam_radius.git;protocol=https;branch=master"
SRCREV="b6442c3e0147f1019990520483fa3a30e4ccf059"

S = "${WORKDIR}/git"

DEPENDS = "libpam"

inherit autotools-brokensep features_check
REQUIRED_DISTRO_FEATURES = "pam"

EXTRA_OECONF = "--disable-developer"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 644 ${S}/pam_radius_auth.conf ${D}${sysconfdir}
    install -d ${D}${base_libdir}/security
    install -m 644 ${S}/pam_radius_auth.so ${D}${base_libdir}/security
}

FILES:${PN} += "${base_libdir}/security/*.so"
FILES:${PN}-dbg += "${base_libdir}/security/.debug"
