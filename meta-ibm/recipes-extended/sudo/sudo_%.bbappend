# Allow passwordless use of sudo

PACKAGECONFIG += "pam-wheel"

do_install:append () {
        # Allow members of the 'wheel' group to use passwordless sudo
        sed -i 's/# \(%wheel ALL=(ALL) NOPASSWD: ALL\)/\1/' ${D}${sysconfdir}/sudoers
}
