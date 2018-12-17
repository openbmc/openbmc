SUMMARY = "lio-utils"
DESCRIPTION = "a simple low-level configuration tool set for the Target+iSCSI (LIO)"
HOMEPAGE = "http://linux-iscsi.org/index.php/Lio-utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=c3ea231a32635cbb5debedf3e88aa3df"

PV = "4.1+git${SRCPV}"

SRC_URI = "git://risingtidesystems.com/lio-utils.git \
           file://0001-Makefiles-Respect-environment-variables-and-add-LDFL.patch \
           "
SRCREV = "28bd928655bdc7bd3cf380f0196630690c51e05f"
S = "${WORKDIR}/git"

inherit distutils

EXTRA_OEMAKE += "DESTDIR=${D}"

do_compile() {
    cd ${S}/tcm-py
    distutils_do_compile

    cd ${S}/lio-py
    distutils_do_compile

    if test -d ${S}/tools; then
        oe_runmake -C ${S}/tools
    fi
}

do_install() {
    cd ${S}/tcm-py
    distutils_do_install

    cd ${S}/lio-py
    distutils_do_install

    SITE_PACKAGES=${D}/${PYTHON_SITEPACKAGES_DIR}
    install -d ${D}/${sbindir}
    for var in tcm_node tcm_dump tcm_loop tcm_fabric lio_dump lio_node; do
        if [ ! -h ${D}/${sbindir}/${var} ];then
            chmod a+x ${SITE_PACKAGES}/${var}.py
            ln -s ${PYTHON_SITEPACKAGES_DIR}/${var}.py ${D}/${sbindir}/${var}
        fi
    done

    if test -d ${S}/tools; then
        oe_runmake -C ${S}/tools install
    fi

    install -d ${D}/etc/target/
    install -d ${D}/etc/init.d/
    install -m 755 ${S}/scripts/rc.target ${D}/etc/init.d/
    install -m 755 ${S}/conf/tcm_start.default ${D}/etc/target/tcm_start.sh
    install -m 755 ${S}/conf/lio_start.default ${D}/etc/target/lio_start.sh
}

RDEPENDS_${PN} += "python-stringold python-subprocess python-shell \
    python-datetime python-textutils python-crypt python-netclient python-email \
    bash"

FILES_${PN} += "${sbindir}/* /etc/init.d/* /etc/target/*"

# http://errors.yoctoproject.org/Errors/Details/184712/
# python-native/python: can't open file 'setup.py': [Errno 2] No such file or directory
CLEANBROKEN = "1"
