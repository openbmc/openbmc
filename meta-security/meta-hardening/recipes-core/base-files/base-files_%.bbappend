
do_install:append:harden () {
    sed -i 's/umask.*/umask 027/g' ${D}/${sysconfdir}/profile
}
