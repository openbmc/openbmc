# 0001-Only-load-dropbear-default-host-keys-if-a-key-is-not.patch
# has been upstreamed.  This patch can be removed once we upgrade
# to yocto 2.5 or later which will pull in the latest dropbear code.
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://dropbearkey.service \
            file://localoptions.h \
            file://dropbear.default \
            file://dropbear-migrate-key-location.service \
            file://migrate-key-location \
           "

do_configure:append() {
    install -m 0644 ${UNPACKDIR}/localoptions.h ${B}
}

# pull in OpenSSH's /usr/libexec/sftp-server so we don't have to rely
# on the crufty old scp protocol for file transfer
RDEPENDS:${PN} += "openssh-sftp-server"

# Add service to migrate the dropbear keys from /var/lib to /etc.
do_install:append() {
    install -d ${D}${base_libdir}/systemd/system
    install -m 0644 ${UNPACKDIR}/dropbear-migrate-key-location.service \
        ${D}${base_libdir}/systemd/system

    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${UNPACKDIR}/migrate-key-location ${D}${libexecdir}/${BPN}
}
SYSTEMD_SERVICE:${PN}:append = " dropbear-migrate-key-location.service"
