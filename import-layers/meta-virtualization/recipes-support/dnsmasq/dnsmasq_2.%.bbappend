# dnsmasq is greedy with interfaces by default using bind-dynamic will
# make it less greedy but still function as it did by default.
do_install_append() {
    sed -i '/#bind-interfaces/a # Play nice with libvirt\nbind-dynamic' ${D}${sysconfdir}/dnsmasq.conf
}
