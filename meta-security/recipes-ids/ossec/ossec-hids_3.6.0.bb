SUMMARY = "A full platform to monitor and control your systems"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d625d1520b5e38faefb81cf9772badc9"


DEPENDS = "openssl libpcre2 zlib libevent"
SRC_URI = "git://github.com/ossec/ossec-hids;branch=master \
           file://0001-Makefile-drop-running-scrips-install.patch  \
           file://0002-Makefile-don-t-set-uid-gid.patch \
           "

SRCREV = "1303c78e2c67d7acee0508cb00c3bc63baaa27c2"

UPSTREAM_CHECK_COMMITS = "1"

inherit autotools-brokensep  useradd

S = "${WORKDIR}/git"

OSSEC_UID ?= "ossec"
OSSEC_RUID ?= "ossecr"
OSSEC_GID ?= "ossec"
OSSEC_EMAIL ?= "ossecm"

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

pkg_postinst_ontarget_${PN} () {
    DIR="/var/ossec"

    usermod -g ossec -G ossec -a root

    # Default for all directories
    chmod -R 550 ${DIR}
    chown -R root:${OSSEC_GID} ${DIR}

    # To the ossec queue (default for agentd to read)
    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${DIR}/queue/ossec
    chmod -R 770 ${DIR}/queue/ossec

    # For the logging user
    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${DIR}/logs
    chmod -R 750 ${DIR}/logs
    chmod -R 775 ${DIR}/queue/rids
    touch ${DIR}/logs/ossec.log
    chown ${OSSEC_UUID}:${OSSEC_GID} ${DIR}/logs/ossec.log
    chmod 664 ${DIR}/logs/ossec.log

    chown -R ${OSSEC_UUID}:${OSSEC_GID} ${DIR}/queue/diff
    chmod -R 750 ${DIR}/queue/diff
        chmod 740 ${DIR}/queue/diff/* > /dev/null 2>&1 || true

	# For the etc dir
	chmod 550 ${DIR}/etc
	chown -R root:${OSSEC_GID} ${DIR}/etc
	if [ -f /etc/localtime ]; then
	    cp -pL /etc/localtime ${DIR}/etc/;
	    chmod 555 ${DIR}/etc/localtime
	    chown root:${OSSEC_GID} ${DIR}/etc/localtime
	fi

	if [ -f /etc/TIMEZONE ]; then
	    cp -p /etc/TIMEZONE ${DIR}/etc/;
	    chmod 555 ${DIR}/etc/TIMEZONE
	fi

	# More files
	chown root:${OSSEC_GID} ${DIR}/etc/internal_options.conf
	chown root:${OSSEC_GID} ${DIR}/etc/local_internal_options.conf >/dev/null 2>&1 || true
	chown root:${OSSEC_GID} ${DIR}/etc/client.keys >/dev/null 2>&1 || true
	chown root:${OSSEC_GID} ${DIR}/agentless/*
	chown ${OSSEC_UUID}:${OSSEC_GID} ${DIR}/.ssh
	chown root:${OSSEC_GID} ${DIR}/etc/shared/*

	chmod 550 ${DIR}/etc
	chmod 440 ${DIR}/etc/internal_options.conf
	chmod 660 ${DIR}/etc/local_internal_options.conf >/dev/null 2>&1 || true
	chmod 440 ${DIR}/etc/client.keys >/dev/null 2>&1 || true
	chmod 550 ${DIR}/agentless/*
	chmod 700 ${DIR}/.ssh
	chmod 770 ${DIR}/etc/shared
	chmod 660 ${DIR}/etc/shared/*

	# For the /var/run
	chmod 770 ${DIR}/var/run
	chown root:${OSSEC_GID} ${DIR}/var/run

	# For util.sh 
	chown root:${OSSEC_GID} ${DIR}/bin/util.sh
	chmod +x ${DIR}/bin/util.sh

	# For binaries and active response
        chmod 755 ${DIR}/active-response/bin/*
        chown root:${OSSEC_GID} ${DIR}/active-response/bin/*
        chown root:${OSSEC_GID} ${DIR}/bin/*
        chmod 550 ${DIR}/bin/*

	# For ossec.conf
        chown root:${OSSEC_GID} ${DIR}/etc/ossec.conf
        chmod 660 ${DIR}/etc/ossec.conf

	# Debconf
	. /usr/share/debconf/confmodule
	db_input high ossec-hids-agent/server-ip || true
	db_go

	db_get ossec-hids-agent/server-ip
	SERVER_IP=$RET

	sed -i "s/<server-ip>[^<]\+<\/server-ip>/<server-ip>${SERVER_IP}<\/server-ip>/" ${DIR}/etc/ossec.conf
	db_stop

        # ossec-init.conf
        if [ -e ${DIR}/etc/ossec-init.conf ] && [ -d /etc/ ]; then
            if [ -e /etc/ossec-init.conf ]; then
                rm -f /etc/ossec-init.conf
            fi
            ln -s ${DIR}/etc/ossec-init.conf /etc/ossec-init.conf
        fi

        # init.d/ossec file
        if [ -x ${DIR}/etc/init.d/ossec ] && [ -d /etc/init.d/ ]; then
            if [ -e /etc/init.d/ossec ]; then
                rm -f /etc/init.d/ossec
            fi
            ln -s ${DIR}/etc/init.d/ossec /etc/init.d/ossec
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
USERADD_PARAM_${PN} = "--system --home-dir /var/ossec -g ossec --shell /bin/false ossec"
GROUPADD_PARAM_${PN} = "--system ossec"

RDEPENDS_${PN} = "openssl bash"
