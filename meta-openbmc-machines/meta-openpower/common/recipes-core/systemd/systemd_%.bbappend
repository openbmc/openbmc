SRC_URI += "${@mf_enabled(d, 'openpower-ubi-fs', 'file://software.conf')}"

do_install_append() {

        # /tmp/images is the software image upload directory.
        # It should not be deleted since it is watched by the Image Manager for
        # new images. Don't install software.conf if obmc-ubi-fs is set since
        # the bbappend in the meta-phosphor layer already installs if
        # obmc-ubi-fs is set.
        if ${@bb.utils.contains('MACHINE_FEATURES', 'openpower-ubi-fs', 'true', 'false', d)} && ! ${@bb.utils.contains('MACHINE_FEATURES', 'obmc-ubi-fs', 'true', 'false', d)}; then
                install -m 0644 ${WORKDIR}/software.conf ${D}${exec_prefix}/lib/tmpfiles.d/
        fi
}
