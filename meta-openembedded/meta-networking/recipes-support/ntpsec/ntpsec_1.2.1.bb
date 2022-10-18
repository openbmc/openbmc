SUMMARY = "The Network Time Protocol suite, refactored"
HOMEPAGE = "https://www.ntpsec.org/"

LICENSE = "CC-BY-4.0 & BSD-2-Clause & NTP & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=0520591566b6ed3a9ced8b15b4d4abf9 \
                    file://libjsmn/LICENSE;md5=38118982429881235de8adf478a8e75d \
                    file://docs/copyright.adoc;md5=9a1e3fce4b630078cb67ba2b619d2b13 \
                    file://libaes_siv/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "bison-native \
            openssl \
            python3"

SRC_URI = "https://ftp.ntpsec.org/pub/releases/ntpsec-${PV}.tar.gz \
           file://0001-Update-to-OpenSSL-3.0.0-alpha15.patch \
           file://0001-ntpd-ntp_sandbox.c-allow-clone3-for-glibc-2.34-in-se.patch \
           file://0001-ntpd-ntp_sandbox.c-allow-newfstatat-on-all-archs-for.patch \
           file://0002-ntpd-ntp_sandbox.c-match-riscv-to-aarch-in-seccomp-f.patch \
           file://volatiles.ntpsec \
           file://0001-wscript-Widen-the-search-for-tags.patch \
           "

SRC_URI[sha256sum] = "f2684835116c80b8f21782a5959a805ba3c44e3a681dd6c17c7cb00cc242c27a"

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
                --enable-debug \
                --enable-debug-gdb \
                --enable-early-droproot"

EXTRA_OEWAF_BUILD ?= "-v"

NTP_USER_HOME ?= "/var/lib/ntp"

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
