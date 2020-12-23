
do_install_append_harden () {
    sed -i 's/umask.*/umask 027/g' ${D}/${sysconfdir}/profile
}
