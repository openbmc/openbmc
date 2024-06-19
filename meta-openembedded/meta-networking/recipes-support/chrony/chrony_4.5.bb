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
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://download.tuxfamily.org/chrony/chrony-${PV}.tar.gz \
    file://chrony.conf \
    file://chronyd \
    file://arm_eabi.patch \
"

SRC_URI:append:libc-musl = " \
    file://0001-Fix-compilation-with-musl.patch \
"
SRC_URI[sha256sum] = "19fe1d9f4664d445a69a96c71e8fdb60bcd8df24c73d1386e02287f7366ad422"

DEPENDS = "pps-tools"

# Note: Despite being built via './configure; make; make install',
#       chrony does not use GNU Autotools.
inherit update-rc.d systemd pkgconfig

# Add chronyd user if privdrop packageconfig is selected
inherit ${@bb.utils.contains('PACKAGECONFIG', 'privdrop', 'useradd', '', d)}
USERADD_PACKAGES = "${@bb.utils.contains('PACKAGECONFIG', 'privdrop', '${PN}', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'privdrop', '--system -d / -M --shell /bin/nologin chronyd;', '', d)}"

# Configuration options:
# - Security-related:
#   - 'sechash' is omitted by default because it pulls in nss which is huge.
#   - 'privdrop' allows chronyd to run as non-root; would need changes to
#     chrony.conf and init script.
#   - 'scfilter' enables support for system call filtering, but requires the
#     kernel to have CONFIG_SECCOMP enabled.
PACKAGECONFIG ??= "editline \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[editline] = ",--without-editline,libedit"
PACKAGECONFIG[sechash] = "--without-tomcrypt,--disable-sechash,nss"
PACKAGECONFIG[privdrop] = ",--disable-privdrop,libcap"
PACKAGECONFIG[scfilter] = "--enable-scfilter,--without-seccomp,libseccomp"
PACKAGECONFIG[ipv6] = ",--disable-ipv6,"

# --disable-static isn't supported by chrony's configure script.
DISABLE_STATIC = ""

do_configure() {
    ./configure --sysconfdir=${sysconfdir} --bindir=${bindir} --sbindir=${sbindir} \
                --localstatedir=${localstatedir} --datarootdir=${datadir} \
                --with-ntp-era=$(shell date -d '1970-01-01 00:00:00+00:00' +'%s') \
                --with-pidfile=/run/chrony/chronyd.pid \
                --chronyrundir=/run/chrony \
                --host-system=Linux \
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
    install -m 644 ${UNPACKDIR}/chrony.conf ${D}${sysconfdir}
    if ${@bb.utils.contains('PACKAGECONFIG', 'privdrop', 'true', 'false', d)}; then
        echo "# Define user to drop to after dropping root privileges" >> ${D}${sysconfdir}/chrony.conf
        echo "user chronyd" >> ${D}${sysconfdir}/chrony.conf
    fi

    # System V init script
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${UNPACKDIR}/chronyd ${D}${sysconfdir}/init.d

    # systemd unit configuration file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/examples/chronyd.service ${D}${systemd_unitdir}/system/

    # Variable data (for drift and/or rtc file)
    install -d ${D}${localstatedir}/lib/chrony

    # Fix hard-coded paths in config files and init scripts
    sed -i -e 's!/var/!${localstatedir}/!g' -e 's!/etc/!${sysconfdir}/!g' \
           -e 's!/usr/sbin/!${sbindir}/!g' -e 's!/usr/bin/!${bindir}/!g' \
           ${D}${sysconfdir}/chrony.conf \
           ${D}${sysconfdir}/init.d/chronyd \
           ${D}${systemd_unitdir}/system/chronyd.service
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/chronyd
    sed -i 's!^EnvironmentFile=.*!EnvironmentFile=-${sysconfdir}/default/chronyd!' ${D}${systemd_unitdir}/system/chronyd.service

    install -d ${D}${sysconfdir}/tmpfiles.d
    echo "d /var/lib/chrony 0755 root root -" > ${D}${sysconfdir}/tmpfiles.d/chronyd.conf

}

FILES:${PN} = "${sbindir}/chronyd ${sysconfdir} ${localstatedir}/lib/chrony ${localstatedir}"
CONFFILES:${PN} = "${sysconfdir}/chrony.conf"
INITSCRIPT_NAME = "chronyd"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "chronyd.service"

# It's probably a bad idea to run chrony and another time daemon on
# the same system.  systemd includes the SNTP client 'timesyncd', which
# will be disabled by chronyd.service, however it will remain on the rootfs
# wasting 150 kB unless you put 'PACKAGECONFIG:remove:pn-systemd = "timesyncd"'
# in a conf file or bbappend somewhere.
RCONFLICTS:${PN} = "ntp ntimed"

# Separate the client program into its own package
PACKAGES =+ "chronyc"
FILES:chronyc = "${bindir}/chronyc"
