
do_install:append_harden () {
    sed -i 's/umask.*/umask 027/g' ${D}/${sysconfdir}/profile
}
