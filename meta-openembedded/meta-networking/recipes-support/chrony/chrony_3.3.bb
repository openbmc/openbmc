SUMMARY = "Versatile implementation of the Network Time Protocol"
DESCRIPTION = "Chrony can synchronize the system clock with NTP \
servers, reference clocks (e.g. GPS receiver), and manual input using \
wristwatch and keyboard. It can also operate as an NTPv4 (RFC 5905) \
server and peer to provide a time service to other computers in the \
network. \
\
It is designed to perform well in a wide range of conditions, \
including intermittent network connections, heavily congested \
networks, changing temperatures (ordinary computer clocks are \
sensitive to temperature), and systems that do not run continuously, or \
run on a virtual machine. \
\
Typical accuracy between two machines on a LAN is in tens, or a few \
hundreds, of microseconds; over the Internet, accuracy is typically \
within a few milliseconds. With a good hardware reference clock \
sub-microsecond accuracy is possible. \
\
Two programs are included in chrony: chronyd is a daemon that can be \
started at boot time and chronyc is a command-line interface program \
which can be used to monitor chronyd's performance and to change \
various operating parameters whilst it is running. \
\
This recipe produces two binary packages: 'chrony' which contains chronyd, \
the configuration file and the init script, and 'chronyc' which contains \
the client program only."

HOMEPAGE = "https://chrony.tuxfamily.org/"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://download.tuxfamily.org/chrony/chrony-${PV}.tar.gz \
    file://chrony.conf \
    file://chronyd \
    file://arm_eabi.patch \
"
SRC_URI[md5sum] = "81ab62cf5d60b4b3fa8cd2c1b267ffd9"
SRC_URI[sha256sum] = "0d1fb2d5875032f2d5a86f3770374c87ee4c941916f64171e81f7684f2a73128"

DEPENDS = "pps-tools"

# Note: Despite being built via './configure; make; make install',
#       chrony does not use GNU Autotools.
inherit update-rc.d systemd

# Configuration options:
# - For command line editing support in chronyc, you may specify either
#   'editline' or 'readline' but not both.  editline is smaller, but
#   many systems already have readline for other purposes so you might want
#   to choose that instead.  However, beware license incompatibility
#   since chrony is GPLv2 and readline versions after 6.0 are GPLv3+.
#   You can of course choose neither, but if you're that tight on space
#   consider dropping chronyc entirely (you can use it remotely with
#   appropriate chrony.conf options).
# - Security-related:
#   - 'sechash' is omitted by default because it pulls in nss which is huge.
#   - 'privdrop' allows chronyd to run as non-root; would need changes to
#     chrony.conf and init script.
#   - 'scfilter' enables support for system call filtering, but requires the
#     kernel to have CONFIG_SECCOMP enabled.
PACKAGECONFIG ??= "editline \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[readline] = "--without-editline,--without-readline,readline"
PACKAGECONFIG[editline] = ",--without-editline,libedit"
PACKAGECONFIG[sechash] = "--without-tomcrypt,--disable-sechash,nss"
PACKAGECONFIG[privdrop] = ",--disable-privdrop,libcap"
PACKAGECONFIG[scfilter] = "--enable-scfilter,--without-seccomp,libseccomp"
PACKAGECONFIG[ipv6] = ",--disable-ipv6,"
PACKAGECONFIG[nss] = "--with-nss,--without-nss,nss"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"

# --disable-static isn't supported by chrony's configure script.
DISABLE_STATIC = ""

do_configure() {
    ./configure --sysconfdir=${sysconfdir} --bindir=${bindir} --sbindir=${sbindir} \
                --localstatedir=${localstatedir} --datarootdir=${datadir} \
                ${PACKAGECONFIG_CONFARGS}
}

do_install() {
    # Binaries
    install -d ${D}${bindir}
    install -m 0755 ${S}/chronyc ${D}${bindir}
    install -d ${D}${sbindir}
    install -m 0755 ${S}/chronyd ${D}${sbindir}

    # Config file
    install -d ${D}${sysconfdir}
    install -m 644 ${WORKDIR}/chrony.conf ${D}${sysconfdir}

    # System V init script
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/chronyd ${D}${sysconfdir}/init.d

    # systemd unit configuration file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/examples/chronyd.service ${D}${systemd_unitdir}/system/

    # Variable data (for drift and/or rtc file)
    install -d ${D}${localstatedir}/lib/chrony

    # Log files
    install -d ${D}${localstatedir}/log/chrony

    # Fix hard-coded paths in config files and init scripts
    sed -i -e 's!/var/!${localstatedir}/!g' -e 's!/etc/!${sysconfdir}/!g' \
           -e 's!/usr/sbin/!${sbindir}/!g' -e 's!/usr/bin/!${bindir}/!g' \
           ${D}${sysconfdir}/chrony.conf \
           ${D}${sysconfdir}/init.d/chronyd \
           ${D}${systemd_unitdir}/system/chronyd.service
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/chronyd
    sed -i 's!^EnvironmentFile=.*!EnvironmentFile=-${sysconfdir}/default/chronyd!' ${D}${systemd_unitdir}/system/chronyd.service
}

FILES_${PN} = "${sbindir}/chronyd ${sysconfdir} ${localstatedir}"
CONFFILES_${PN} = "${sysconfdir}/chrony.conf"
INITSCRIPT_NAME = "chronyd"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "chronyd.service"

# It's probably a bad idea to run chrony and another time daemon on
# the same system.  systemd includes the SNTP client 'timesyncd', which
# will be disabled by chronyd.service, however it will remain on the rootfs
# wasting 150 kB unless you put 'PACKAGECONFIG_remove_pn-systemd = "timesyncd"'
# in a conf file or bbappend somewhere.
RCONFLICTS_${PN} = "ntp ntimed"

# Separate the client program into its own package
PACKAGES =+ "chronyc"
FILES_chronyc = "${bindir}/chronyc"
