SUMMARY = "A full platform to monitor and control your systems"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d625d1520b5e38faefb81cf9772badc9"


DEPENDS = "openssl libpcre2 zlib libevent"
SRC_URI = "git://github.com/ossec/ossec-hids;branch=master;protocol=https \
           file://0001-Makefile-drop-running-scrips-install.patch  \
           file://0002-Makefile-don-t-set-uid-gid.patch \
           "

SRCREV = "bf797c759994015274f3bc31fe2bed278cce67ee"

UPSTREAM_CHECK_COMMITS = "1"

inherit autotools-brokensep  useradd

S = "${UNPACKDIR}/git"


OSSEC_DIR="/var/ossec"
OSSEC_UID ?= "ossec"
OSSEC_RUID ?= "ossecr"
OSSEC_GID ?= "ossec"
OSSEC_EMAIL ?= "ossecm"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${OSSEC_UID}"
USERADD_PARAM:${PN} = "--system -g ${OSSEC_GID} --home-dir  \
                       ${OSSEC_DIR} --no-create-home  \
                       --shell /sbin/nologin ${BPN}"

do_configure[noexec] = "1"

do_compile() {
  cd ${S}/src
  make PREFIX=${prefix} TARGET=local USE_SYSTEMD=No build 
}

do_install(){
  install -d ${D}${sysconfdir}
  install -d ${D}/var/ossec/${sysconfdir}

  cd ${S}/src
  make TARGET=local  PREFIX=${D}/var/ossec install

  echo "DIRECTORY=\"/var/ossec\"" > ${D}/${sysconfdir}/ossec-init.conf
  echo "VERSION=\"${PV}\"" >> ${D}/${sysconfdir}/ossec-init.conf
  echo "DATE=\"`date`\"" >> ${D}/${sysconfdir}/ossec-init.conf
  echo "TYPE=\"local\"" >> ${D}/${sysconfdir}/ossec-init.conf
  chmod 600  ${D}/${sysconfdir}/ossec-init.conf
  install -m 640 ${D}/${sysconfdir}/ossec-init.conf ${D}/var/ossec/${sysconfdir}/ossec-init.conf
}

pkg_postinst_ontarget:${PN} () {

    # Default for all directories
    chmod -R 550 ${OSSEC_DIR}
    chown -R root:${OSSEC_GID} ${OSSEC_DIR}

    # To the ossec queue (default for agentd to read)
    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${OSSEC_DIR}/queue/ossec
    chmod -R 770 ${OSSEC_DIR}/queue/ossec

    # For the logging user
    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${OSSEC_DIR}/logs
    chmod -R 750 ${OSSEC_DIR}/logs
    chmod -R 775 ${OSSEC_DIR}/queue/rids
    touch ${OSSEC_DIR}/logs/ossec.log
    chown ${OSSEC_UUID}:${OSSEC_GID} ${OSSEC_DIR}/logs/ossec.log
    chmod 664 ${OSSEC_DIR}/logs/ossec.log

    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${OSSEC_DIR}/queue/diff
    chmod -R 750 ${OSSEC_DIR}/queue/diff
    chmod 740 ${OSSEC_DIR}/queue/diff/* > /dev/null 2>&1 || true

	# For the etc dir
	chmod 550 ${OSSEC_DIR}/etc
	chown -R root:${OSSEC_GID} ${OSSEC_DIR}/etc
	if [ -f /etc/localtime ]; then
	    cp -pL /etc/localtime ${OSSEC_DIR}/etc/;
	    chmod 555 ${OSSEC_DIR}/etc/localtime
	    chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/localtime
	fi

	if [ -f /etc/TIMEZONE ]; then
	    cp -p /etc/TIMEZONE ${OSSEC_DIR}/etc/;
	    chmod 555 ${OSSEC_DIR}/etc/TIMEZONE
	fi

	# More files
	chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/internal_options.conf
	chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/local_internal_options.conf >/dev/null 2>&1 || true
	chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/client.keys >/dev/null 2>&1 || true
	chown root:${OSSEC_GID} ${OSSEC_DIR}/agentless/*
	chown ${OSSEC_UUID}:${OSSEC_GID} ${OSSEC_DIR}/.ssh
	chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/shared/*

	chmod 550 ${OSSEC_DIR}/etc
	chmod 440 ${OSSEC_DIR}/etc/internal_options.conf
	chmod 660 ${OSSEC_DIR}/etc/local_internal_options.conf >/dev/null 2>&1 || true
	chmod 440 ${OSSEC_DIR}/etc/client.keys >/dev/null 2>&1 || true
	chmod 550 ${OSSEC_DIR}/agentless/*
	chmod 700 ${OSSEC_DIR}/.ssh
	chmod 770 ${OSSEC_DIR}/etc/shared
	chmod 660 ${OSSEC_DIR}/etc/shared/*

	# For the /var/run
	chmod 770 ${OSSEC_DIR}/var/run
	chown root:${OSSEC_GID} ${OSSEC_DIR}/var/run

	# For util.sh 
	chown root:${OSSEC_GID} ${OSSEC_DIR}/bin/util.sh
	chmod +x ${OSSEC_DIR}/bin/util.sh

	# For binaries and active response
        chmod 755 ${OSSEC_DIR}/active-response/bin/*
        chown root:${OSSEC_GID} ${OSSEC_DIR}/active-response/bin/*
        chown root:${OSSEC_GID} ${OSSEC_DIR}/bin/*
        chmod 550 ${OSSEC_DIR}/bin/*

	# For ossec.conf
        chown root:${OSSEC_GID} ${OSSEC_DIR}/etc/ossec.conf
        chmod 660 ${OSSEC_DIR}/etc/ossec.conf

	# Debconf
	. /usr/share/debconf/confmodule
	db_input high ossec-hids-agent/server-ip || true
	db_go

	db_get ossec-hids-agent/server-ip
	SERVER_IP=$RET

	sed -i "s/<server-ip>[^<]\+<\/server-ip>/<server-ip>${SERVER_IP}<\/server-ip>/" ${OSSEC_DIR}/etc/ossec.conf
	db_stop

        # ossec-init.conf
        if [ -e ${OSSEC_DIR}/etc/ossec-init.conf ] && [ -d /etc/ ]; then
            if [ -e /etc/ossec-init.conf ]; then
                rm -f /etc/ossec-init.conf
            fi
            ln -s ${OSSEC_DIR}/etc/ossec-init.conf /etc/ossec-init.conf
        fi

        # init.d/ossec file
        if [ -x ${OSSEC_DIR}/etc/init.d/ossec ] && [ -d /etc/init.d/ ]; then
            if [ -e /etc/init.d/ossec ]; then
                rm -f /etc/init.d/ossec
            fi
            ln -s ${OSSEC_DIR}/etc/init.d/ossec /etc/init.d/ossec
        fi

	# Service
	if [ -x /etc/init.d/ossec ]; then
	    update-rc.d -f ossec defaults
	fi

	# Delete tmp directory
	if [ -d ${OSSEC_HIDS_TMP_DIR} ]; then
	    rm -r ${OSSEC_HIDS_TMP_DIR}
	fi
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home-dir /var/ossec -g ossec --shell /bin/false ossec"
GROUPADD_PARAM:${PN} = "--system ossec"

RDEPENDS:${PN} = "openssl bash"

COMPATIBLE_HOST:libc-musl = "null"
