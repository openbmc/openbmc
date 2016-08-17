SUMMARY = "HTML5 (plugin-free) web-based terminal emulator and SSH client"
LICENSE = "AGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=834cbc6995db88433db17cdf8953a428"
HOMEPAGE = "http://liftoffsoftware.com/Products/GateOne"

PV = "1.2+git${SRCPV}"
SRCREV = "f7a9be46cb90f57459ebd363d24702de0e651034"
SRC_URI = "git://github.com/liftoff/GateOne.git;branch=master \
           file://gateone-avahi.service \
           file://80oe.conf.in \
           file://gateone.service.in \
           file://gateone-init.in \
"

S = "${WORKDIR}/git"

inherit distutils python-dir systemd update-rc.d
export prefix = "${localstatedir}"

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${PYTHON_SITEPACKAGES_DIR} \
    --install-scripts=${bindir} \
    --skip_init_scripts"

do_install_append() {

    # fix up hardcoded paths
    for file in gateone.service gateone-init 80oe.conf; do
        sed -e s:@bindir@:${bindir}:g \
            -e s:@localstate@:${localstatedir}:g \
            < ${WORKDIR}/$file.in \
            > ${WORKDIR}/$file
    done
    
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/gateone.service ${D}${systemd_unitdir}/system

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/gateone-init ${D}${sysconfdir}/init.d/gateone
    
    install -m 0755 -d ${D}${sysconfdir}/avahi/services/
    install -m 0644 ${WORKDIR}/gateone-avahi.service ${D}${sysconfdir}/avahi/services/

    install -m 0755 -d ${D}${sysconfdir}/gateone/conf.d/
    install -m 0644 ${WORKDIR}/80oe.conf ${D}${sysconfdir}/gateone/conf.d/80oe.conf

    install -d ${D}${localstatedir}/lib/gateone
}

FILES_${PN} = "${localstatedir}/lib ${bindir} ${base_libdir} ${sysconfdir} ${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS_${PN} = "mime-support \
                  openssh-ssh \
                  python-compression \
                  python-contextlib \
                  python-crypt \
                  python-datetime \
                  python-email \
                  python-fcntl \
                  python-futures \
                  python-html \
                  python-imaging \
                  python-io \
                  python-json \
                  python-logging \
                  python-misc \
                  python-multiprocessing \
                  python-netclient \
                  python-pkgutil \
                  python-pyopenssl \
                  python-re \
                  python-readline \
                  python-setuptools \
                  python-shell \
                  python-simplejson \
                  python-subprocess \
                  python-syslog \
                  python-terminal \
                  python-textutils \
                  python-tornado \
                  python-unixadmin \
                  python-xml \
                  python-html5lib \
                  bash \
"

SYSTEMD_SERVICE_${PN} = "gateone.service"
INITSCRIPT_NAME = "gateone"
