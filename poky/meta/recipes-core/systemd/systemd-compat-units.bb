SUMMARY = "Enhances systemd compatilibity with existing SysVinit scripts"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"
LICENSE = "MIT"


PACKAGE_WRITE_DEPS += "systemd-systemctl-native"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check

INHIBIT_DEFAULT_DEPS = "1"

ALLOW_EMPTY:${PN} = "1"

REQUIRED_DISTRO_FEATURES += "systemd"
REQUIRED_DISTRO_FEATURES += "usrmerge"

SYSTEMD_DISABLED_SYSV_SERVICES = " \
  busybox-udhcpc \
  hwclock \
  networking \
  nfsserver \
  nfscommon \
  syslog.busybox \
"

pkg_postinst:${PN} () {

	test -d $D${sysconfdir}/init.d  ||  exit 0
	cd $D${sysconfdir}/init.d

	echo "Disabling the following sysv scripts: "

	if [ -n "$D" ]; then
		OPTS="--root=$D"
	else
		OPTS=""
	fi

	for i in ${SYSTEMD_DISABLED_SYSV_SERVICES} ; do
		if [ -e $i -o -e $i.sh ]  &&   ! [ -e $D${sysconfdir}/systemd/system/$i.service -o -e $D${systemd_system_unitdir}/$i.service ] ; then
			echo -n "$i: "
			systemctl $OPTS mask $i.service
		fi
	done
	echo
}

RDEPENDS:${PN} = "systemd"
