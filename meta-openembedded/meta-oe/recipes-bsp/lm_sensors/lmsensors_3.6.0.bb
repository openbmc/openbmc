SUMMARY = "Hardware health monitoring applications"
HOMEPAGE = "https://hwmon.wiki.kernel.org/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    bison-native \
    flex-native \
    virtual/libiconv \
"

SRC_URI = "git://github.com/lm-sensors/lm-sensors.git;protocol=https;branch=master \
           file://fancontrol.init \
           file://sensord.init \
           file://0001-Change-PIDFile-path-from-var-run-to-run.patch \
           file://0001-Fix-building-with-GCC-14.patch \
"
SRCREV = "1667b850a1ce38151dae17156276f981be6fb557"

inherit update-rc.d systemd

RDEPENDS:${PN}-dev = ""

PACKAGECONFIG ??= "sensord"
PACKAGECONFIG[sensord] = "sensord,,rrdtool"

INITSCRIPT_PACKAGES = "\
    ${PN}-fancontrol \
    ${@bb.utils.contains('PACKAGECONFIG', 'sensord', '${PN}-sensord', '', d)} \
    "
INITSCRIPT_NAME:${PN}-fancontrol = "fancontrol"
INITSCRIPT_NAME:${PN}-sensord = "sensord"
INITSCRIPT_PARAMS:${PN}-fancontrol = "defaults 66"
INITSCRIPT_PARAMS:${PN}-sensord = "defaults 67"

SYSTEMD_PACKAGES = "\
    ${PN} \
    ${PN}-fancontrol \
    ${@bb.utils.contains('PACKAGECONFIG', 'sensord', '${PN}-sensord', '', d)} \
    "
SYSTEMD_SERVICE:${PN} = "lm_sensors.service"
SYSTEMD_SERVICE:${PN}-fancontrol = "fancontrol.service"
SYSTEMD_SERVICE:${PN}-sensord = "sensord.service"
SYSTEMD_AUTO_ENABLE = "disable"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = 'EXLDFLAGS="${LDFLAGS}" \
        MACHINE=${TARGET_ARCH} PREFIX=${prefix} MANDIR=${mandir} \
        LIBDIR=${libdir} \
        CC="${CC}" AR="${AR}"'

do_compile() {
    sed -i -e 's:^# \(PROG_EXTRA\):\1:' ${S}/Makefile
    # Respect LDFLAGS
    sed -i -e 's/\$(LIBDIR)$/\$(LIBDIR) \$(LDFLAGS)/g' ${S}/Makefile
    sed -i -e 's/\$(LIBSHSONAME) -o/$(LIBSHSONAME) \$(LDFLAGS) -o/g' \
                ${S}/lib/Module.mk
    oe_runmake user PROG_EXTRA="sensors ${PACKAGECONFIG_CONFARGS}"
}

do_install() {
    oe_runmake user_install DESTDIR=${D}
    if ${@bb.utils.contains('PACKAGECONFIG', 'sensord', 'true', 'false', d)}; then
        install -m 0755 ${S}/prog/sensord/sensord ${D}${sbindir}
        install -m 0644 ${S}/prog/sensord/sensord.8 ${D}${mandir}/man8
    fi

    # Install directory
    install -d ${D}${INIT_D_DIR}

    # Install fancontrol init script
    install -m 0755 ${UNPACKDIR}/fancontrol.init ${D}${INIT_D_DIR}/fancontrol

    if ${@bb.utils.contains('PACKAGECONFIG', 'sensord', 'true', 'false', d)}; then
        # Install sensord init script
        install -m 0755 ${UNPACKDIR}/sensord.init ${D}${INIT_D_DIR}/sensord
    fi

    # Insall sensord service script
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/prog/init/*.service ${D}${systemd_unitdir}/system
    if ! ${@bb.utils.contains('PACKAGECONFIG', 'sensord', 'true', 'false', d)}; then
        rm ${D}${systemd_system_unitdir}/sensord.service
    fi
}

RPROVIDES:${PN}-dbg += "${PN}-libsensors-dbg ${PN}-sensors-dbg ${PN}-sensord-dbg ${PN}-isatools-dbg"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} += " \
    ${PN}-libsensors \
    ${PN}-sensors \
    ${@bb.utils.contains('PACKAGECONFIG', 'sensord', '${PN}-sensord', '', d)} \
    ${PN}-fancontrol \
    ${PN}-sensorsdetect \
    ${PN}-sensorsconfconvert \
    ${PN}-pwmconfig \
    ${@bb.utils.contains('MACHINE_FEATURES', 'x86', '${PN}-isatools', '', d)} \
"

# libsensors packages
PACKAGES =+ "${PN}-libsensors ${PN}-libsensors-dev ${PN}-libsensors-staticdev ${PN}-libsensors-doc"

# sensors command packages
PACKAGES =+ "${PN}-sensors ${PN}-sensors-doc"

# sensord logging daemon
PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'sensord', '${PN}-sensord ${PN}-sensord-doc', '', d)}"

# fancontrol script
PACKAGES =+ "${PN}-fancontrol ${PN}-fancontrol-doc"

# sensors-detect script
PACKAGES =+ "${PN}-sensorsdetect ${PN}-sensorsdetect-doc"

# sensors-conf-convert script
PACKAGES =+ "${PN}-sensorsconfconvert ${PN}-sensorsconfconvert-doc"

# pwmconfig script
PACKAGES =+ "${PN}-pwmconfig ${PN}-pwmconfig-doc"

# isadump and isaset helper program
PACKAGES =+ "${PN}-isatools ${PN}-isatools-doc"


# libsensors files
FILES:${PN}-libsensors = "${libdir}/libsensors.so.* ${sysconfdir}/sensors3.conf ${sysconfdir}/sensors.d"
FILES:${PN}-libsensors-dev = "${libdir}/libsensors.so ${includedir}"
FILES:${PN}-libsensors-staticdev = "${libdir}/libsensors.a"
FILES:${PN}-libsensors-doc = "${mandir}/man3"
RRECOMMENDS:${PN}-libsensors = "lmsensors-config-libsensors"

# sensors command files
FILES:${PN}-sensors = "${bindir}/sensors"
FILES:${PN}-sensors-doc = "${mandir}/man1 ${mandir}/man5"
RDEPENDS:${PN}-sensors = "${PN}-libsensors"

# sensord logging daemon
FILES:${PN}-sensord = "${sbindir}/sensord ${INIT_D_DIR}/sensord ${systemd_system_unitdir}/sensord.service"
FILES:${PN}-sensord-doc = "${mandir}/man8/sensord.8"
RDEPENDS:${PN}-sensord = "${PN}-sensors rrdtool"
RRECOMMENDS:${PN}-sensord = "lmsensors-config-sensord"

# fancontrol script files
FILES:${PN}-fancontrol = "${sbindir}/fancontrol ${INIT_D_DIR}/fancontrol"
FILES:${PN}-fancontrol-doc = "${mandir}/man8/fancontrol.8"
RDEPENDS:${PN}-fancontrol = "bash"
RRECOMMENDS:${PN}-fancontrol = "lmsensors-config-fancontrol"

# sensors-detect script files
FILES:${PN}-sensorsdetect = "${sbindir}/sensors-detect"
FILES:${PN}-sensorsdetect-doc = "${mandir}/man8/sensors-detect.8"
RDEPENDS:${PN}-sensorsdetect = "${PN}-sensors perl perl-module-fcntl perl-module-file-basename \
	perl-module-strict perl-module-constant"

# sensors-conf-convert script files
FILES:${PN}-sensorsconfconvert = "${bindir}/sensors-conf-convert"
FILES:${PN}-sensorsconfconvert-doc = "${mandir}/man8/sensors-conf-convert.8"
RDEPENDS:${PN}-sensorsconfconvert = "${PN}-sensors perl perl-module-strict perl-module-vars"

# pwmconfig script files
FILES:${PN}-pwmconfig = "${sbindir}/pwmconfig"
FILES:${PN}-pwmconfig-doc = "${mandir}/man8/pwmconfig.8"
RDEPENDS:${PN}-pwmconfig = "${PN}-fancontrol bash"

# isadump and isaset helper program files
FILES:${PN}-isatools = "${sbindir}/isa*"
FILES:${PN}-isatools-doc = "${mandir}/man8/isa*"
