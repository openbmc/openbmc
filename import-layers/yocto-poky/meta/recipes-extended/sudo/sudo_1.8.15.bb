require sudo.inc

SRC_URI = "http://ftp.sudo.ws/sudo/dist/sudo-${PV}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} \
           file://0001-Include-sys-types.h-for-id_t-definition.patch \
           "

PAM_SRC_URI = "file://sudo.pam"

SRC_URI[md5sum] = "7cf6b9b76d0478a572432bed481dd7b5"
SRC_URI[sha256sum] = "4316381708324da8b6cb151f655c1a11855207c7c02244d8ffdea5104d7cc308"

DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
RDEPENDS_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-limits pam-plugin-keyinit', '', d)}"

EXTRA_OECONF += " \
             ac_cv_type_rsize_t=no \
             ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--with-pam', '--without-pam', d)} \
             ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-tmpfiles.d=${libdir}/tmpfiles.d', '--disable-tmpfiles.d', d)} \
             "

do_install_append () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -D -m 644 ${WORKDIR}/sudo.pam ${D}/${sysconfdir}/pam.d/sudo
	fi

	chmod 4111 ${D}${bindir}/sudo
	chmod 0440 ${D}${sysconfdir}/sudoers

	# Explicitly remove the ${localstatedir}/run directory to avoid QA error
	rmdir -p --ignore-fail-on-non-empty ${D}${localstatedir}/run/sudo
}

FILES_${PN} += "${libdir}/tmpfiles.d"
FILES_${PN}-dev += "${libexecdir}/${BPN}/lib*${SOLIBSDEV} ${libexecdir}/${BPN}/*.la \
                    ${libexecdir}/lib*${SOLIBSDEV} ${libexecdir}/*.la"
