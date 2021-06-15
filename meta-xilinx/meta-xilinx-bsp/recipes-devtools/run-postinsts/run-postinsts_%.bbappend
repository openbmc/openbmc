# Update-alternatives is not able to find stdout when using JTAG boot mode on
# our devices, exits ungracefully without performing the required work (symbolic
# linking), pass kmsg to it as output to achieve proper behavior.

do_install_append(){
    sed -i "s/sh -c \$i \$append_log/sh -c \$i > \/dev\/kmsg/" ${D}${sbindir}/run-postinsts
}
