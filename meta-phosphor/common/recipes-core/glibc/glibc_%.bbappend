#Remove /sbin/ldconfig and /etc/ld.so.conf
do_install_append () {
        rm -f ${D}${sysconfdir}/ld.so.conf
        rm -f ${D}${base_sbindir}/ldconfig
}
