SUMMARY = "Basic networking tools"
DESCRIPTION = "A collection of programs that form the base set of the NET-3 networking distribution for the Linux operating system"
HOMEPAGE = "http://net-tools.berlios.de/"
BUGTRACKER = "http://bugs.debian.org/net-tools"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://ifconfig.c;beginline=11;endline=15;md5=d1ca372080ad5401e23ca0afc35cf9ba"

PV = "1.60-20181103+git${SRCPV}"
SRCREV = "0eebece8c964e3cfa8a018f42b2e7e751a7009a0"
SRC_URI = "git://git.code.sf.net/p/net-tools/code;protocol=https \
    file://net-tools-config.h \
    file://net-tools-config.make \
    file://Add_missing_headers.patch \
    file://Bug_443075-ifconfig.c-pointtopoint_spelling.patch \
    file://Bug_541172-netstat.c-exit-codes.patch \
"

S = "${WORKDIR}/git"

inherit gettext

# The Makefile is lame, no parallel build
PARALLEL_MAKE = ""

PACKAGECONFIG ??= "hostname arp serial plip"
PACKAGECONFIG[hostname] = ""
PACKAGECONFIG[arp] = ""
PACKAGECONFIG[serial] = ""
PACKAGECONFIG[plip] = ""

do_configure() {
	# net-tools has its own config mechanism requiring "make config"
	# we pre-generate desired options and copy to source directory instead
	cp ${WORKDIR}/net-tools-config.h    ${S}/config.h
	cp ${WORKDIR}/net-tools-config.make ${S}/config.make

	if [ "${USE_NLS}" = "no" ]; then
		sed -i -e 's/^I18N=1/# I18N=1/' ${S}/config.make
	fi

	if ${@bb.utils.contains('PACKAGECONFIG', 'hostname', 'true', 'false', d)} ; then
		echo "#define HAVE_HOSTNAME_TOOLS 1" >> ${S}/config.h
		echo "#define HAVE_HOSTNAME_SYMLINKS 1" >> ${S}/config.h
		echo "HAVE_HOSTNAME_TOOLS=1" >> ${S}/config.make
		echo "HAVE_HOSTNAME_SYMLINKS=1" >> ${S}/config.make
	fi
	if ${@bb.utils.contains('PACKAGECONFIG', 'arp', 'true', 'false', d)} ; then
		echo "#define HAVE_ARP_TOOLS 1" >> ${S}/config.h
		echo "HAVE_ARP_TOOLS=1" >> ${S}/config.make
	fi
	if ${@bb.utils.contains('PACKAGECONFIG', 'serial', 'true', 'false', d)} ; then
		echo "#define HAVE_SERIAL_TOOLS 1" >> ${S}/config.h
		echo "HAVE_SERIAL_TOOLS=1" >> ${S}/config.make
	fi
	if ${@bb.utils.contains('PACKAGECONFIG', 'plip', 'true', 'false', d)} ; then
		echo "#define HAVE_PLIP_TOOLS 1" >> ${S}/config.h
		echo "HAVE_PLIP_TOOLS=1" >> ${S}/config.make
	fi
}

do_compile() {
	# net-tools use COPTS/LOPTS to allow adding custom options
	oe_runmake COPTS="$CFLAGS" LOPTS="$LDFLAGS"
}

do_install() {
	# We don't need COPTS or LOPTS, but let's be consistent.
	oe_runmake COPTS="$CFLAGS" LOPTS="$LDFLAGS" BASEDIR=${D} INSTALLNLSDIR=${D}${datadir}/locale mandir=${mandir} install

	if [ "${base_bindir}" != "/bin" ]; then
		mkdir -p ${D}/${base_bindir}
		mv ${D}/bin/* ${D}/${base_bindir}/
		rmdir ${D}/bin
	fi
	if [ "${base_sbindir}" != "/sbin" ]; then
		mkdir ${D}/${base_sbindir}
		mv ${D}/sbin/* ${D}/${base_sbindir}/
		rmdir ${D}/sbin
	fi
}

inherit update-alternatives

base_sbindir_progs = "ipmaddr iptunnel mii-tool nameif \
    ${@bb.utils.contains('PACKAGECONFIG', 'arp', 'arp rarp', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'plip', 'plipconfig', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'serial', 'slattach', '', d)} \
"
base_bindir_progs  = "ifconfig netstat route \
    ${@bb.utils.contains('PACKAGECONFIG', 'hostname', 'dnsdomainname domainname hostname nisdomainname ypdomainname', '', d)} \
"

ALTERNATIVE_${PN} = "${base_sbindir_progs} ${base_bindir_progs}"
ALTERNATIVE_${PN}-doc += "${@bb.utils.contains('PACKAGECONFIG', 'hostname', 'hostname.1 dnsdomainname.1', '', d)}"
ALTERNATIVE_LINK_NAME[hostname.1] = "${mandir}/man1/hostname.1"
ALTERNATIVE_LINK_NAME[dnsdomainname.1] = "${mandir}/man1/dnsdomainname.1"
ALTERNATIVE_PRIORITY[hostname.1] = "10"

python __anonymous() {
    for prog in d.getVar('base_sbindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_sbindir'), prog))
    for prog in d.getVar('base_bindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))
}
ALTERNATIVE_PRIORITY = "100"

NETTOOLS_PACKAGES = "${PN}-mii-tool"
NETTOOLS_PACKAGES_class-native = ""

PACKAGE_BEFORE_PN = "${NETTOOLS_PACKAGES}"
RDEPENDS_${PN} += "${NETTOOLS_PACKAGES}"

FILES_${PN}-mii-tool = "${base_sbindir}/mii-tool"

ALTERNATIVE_${PN}_remove = "mii-tool"

ALTERNATIVE_${PN}-mii-tool = "mii-tool"
ALTERNATIVE_TARGET[mii-tool] = "${base_sbindir}/mii-tool"
ALTERNATIVE_LINK_NAME[mii-tool] = "${base_sbindir}/mii-tool"

BBCLASSEXTEND = "native nativesdk"