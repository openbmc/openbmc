SUMMARY = "The Network Time Protocol suite, refactored"
HOMEPAGE = "https://www.ntpsec.org/"

LICENSE = "CC-BY-4.0 & BSD-2-Clause & NTP & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSES/BSD-2;md5=653830da7b770a32f6f50f6107e0b186 \
                    file://LICENSES/BSD-3;md5=55e9dcf6a625a2dcfcda4ef6a647fbfd \
                    file://LICENSES/CC-BY-4.0;md5=2ab724713fdaf49e4523c4503bfd068d \
                    file://LICENSES/MIT;md5=5a9dfc801af3eb49df2055c9b07918b2 \
                    file://LICENSES/NTP;md5=cb56b7747f86157c78ca81f224806694"

DEPENDS += "bison-native \
            openssl \
            python3"

SRC_URI = "https://ftp.ntpsec.org/pub/releases/ntpsec-${PV}.tar.gz \
           file://volatiles.ntpsec \
           file://0001-wscript-Add-BISONFLAGS-support.patch \
           "

SRC_URI[sha256sum] = "e0ce93af222a0a9860e6f5a51aadba9bb5ca601d80b2aea118a62f0a3226950e"

UPSTREAM_CHECK_URI = "ftp://ftp.ntpsec.org/pub/releases/"

inherit pkgconfig python3-dir python3targetconfig systemd update-alternatives update-rc.d useradd waf features_check

# RDEPENDS on gnuplot with this restriction
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'seccomp systemd', d)} \
                 cap \
                 leap-smear \
                 mdns \
                 mssntp \
                 nts \
                 refclocks"

PACKAGECONFIG:remove:riscv32 = "seccomp"

PACKAGECONFIG[cap] = ",,libcap"
PACKAGECONFIG[docs] = "--enable-doc --enable-manpage,--disable-doc --disable-manpage,"
PACKAGECONFIG[leap-smear] = "--enable-leap-smear"
PACKAGECONFIG[mdns] = ",,mdns"
PACKAGECONFIG[mssntp] = "--enable-mssntp"
PACKAGECONFIG[nts] = ",--disable-nts"
PACKAGECONFIG[refclocks] = "--refclock=all,,pps-tools"
PACKAGECONFIG[seccomp] = "--enable-seccomp,,libseccomp"
PACKAGECONFIG[systemd] = ",,systemd"

CC[unexport] = "1"
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"

export PYTHON_VERSION = "${PYTHON_BASEVERSION}"
export PYTAG = "cpython${@ d.getVar('PYTHON_BASEVERSION').replace('.', '')}"
export pyext_PATTERN = "%s.so"
export PYTHON_LDFLAGS = "-lpthread -ldl"

CFLAGS:append = " -I${PYTHON_INCLUDE_DIR} -D_GNU_SOURCE"

EXTRA_OECONF = "--cross-compiler='${CC}' \
                --cross-cflags='${CFLAGS}' \
                --cross-ldflags='${LDFLAGS}' \
                --pyshebang=${bindir}/python3 \
                --pythondir=${PYTHON_SITEPACKAGES_DIR} \
                --pythonarchdir=${PYTHON_SITEPACKAGES_DIR} \
                --enable-debug-gdb \
                --enable-early-droproot"

EXTRA_OEWAF_BUILD ?= "-v"

NTP_USER_HOME ?= "/var/lib/ntp"

BISONFLAGS = "--file-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}"

do_configure:prepend() {
	export BISONFLAGS="${BISONFLAGS}"
}

do_install:append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${S}/etc/rc/ntpd ${D}${sysconfdir}/init.d
	cp -r ${S}/etc/ntp.d ${D}${sysconfdir}

	sed -e 's:@NTP_USER_HOME@:${NTP_USER_HOME}:g' ${WORKDIR}/volatiles.ntpsec >${T}/volatiles.ntpsec
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		cp ${B}/main/etc/* ${D}${systemd_system_unitdir}
		awk '{print $1, $5, $4, $2, $3, "-"}' ${T}/volatiles.ntpsec >${T}/tmpfiles.ntpsec
		install -D -m 0644 ${T}/tmpfiles.ntpsec ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
	else
		install -D -m 0644 ${T}/volatiles.ntpsec ${D}${sysconfdir}/default/volatiles/99_${BPN}
	fi
}

PACKAGE_BEFORE_PN = "${PN}-python ${PN}-utils ${PN}-viz"

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d/ntpsec.conf"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR} \
                      ${libdir}/libntpc.so.*"
FILES:${PN}-utils = "${bindir}/ntpdig \
                     ${bindir}/ntpkeygen \
                     ${bindir}/ntpleapfetch \
                     ${bindir}/ntpmon \
                     ${bindir}/ntpq \
                     ${bindir}/ntpsnmpd \
                     ${bindir}/ntpsweep \
                     ${bindir}/ntptrace \
                     ${bindir}/ntpwait"
FILES:${PN}-viz = "${bindir}/ntplogtemp \
                   ${bindir}/ntpviz"

RDEPENDS:${PN} += "libgcc"
RDEPENDS:${PN}-utils += "${PN}-python python3-core"
RDEPENDS:${PN}-viz += "gnuplot ${PN}-python python3-core python3-compression python3-ctypes python3-logging python3-shell"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home-dir ${NTP_USER_HOME} \
                       --no-create-home \
                       --shell /bin/false --user-group ntp"

INITSCRIPT_NAME = "ntpd"

SYSTEMD_PACKAGES = "${PN} ${PN}-viz"
SYSTEMD_SERVICE:${PN} = "ntpd.service ntp-wait.service"
SYSTEMD_SERVICE:${PN}-viz = "ntplogtemp.service ntpviz-weekly.timer ntpviz-weekly.service ntpviz-daily.timer ntpviz-daily.service ntplogtemp.timer"

ALTERNATIVE_PRIORITY = "80"

ALTERNATIVE:${PN} = "ntpd"
ALTERNATIVE_LINK_NAME[ntpd] = "${sbindir}/ntpd"
