SRC_URI += "${@df_enabled(d, 'openpower-ubi-fs', 'file://software.conf')}"

install_tmpfile() {
        # /tmp/images is the software image upload directory.
        # It should not be deleted since it is watched by the Image Manager for
        # new images.

        if ${@bb.utils.contains('DISTRO_FEATURES', 'openpower-ubi-fs', 'true', 'false', d)}; then
                install -m 0644 ${WORKDIR}/software.conf ${D}${exec_prefix}/lib/tmpfiles.d/
        fi
}

install_tmpfile_df-obmc-ubi-fs() {
        # Don't install software.conf if obmc-ubi-fs is set since
        # the bbappend in the meta-phosphor layer already installs
        # if obmc-ubi-fs is set.

        :
}

do_install_append() {
        install_tmpfile
}
