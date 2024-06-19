require timezone.inc

DEPENDS = "tzcode-native"

inherit allarch

DEFAULT_TIMEZONE ?= "Universal"
INSTALL_TIMEZONE_FILE ?= "1"

TZONES = " \
    africa antarctica asia australasia europe northamerica southamerica \
    factory etcetera backward \
"

# "slim" is the default since 2020b
# "fat" is needed by e.g. MariaDB's mysql_tzinfo_to_sql
ZIC_FMT ?= "slim"

do_configure[cleandirs] = "${B}"
B = "${WORKDIR}/build"

do_compile() {
	oe_runmake -C ${S} tzdata.zi
	for zone in ${TZONES}; do
		${STAGING_BINDIR_NATIVE}/zic -b ${ZIC_FMT} -d ${B}/zoneinfo -L /dev/null ${S}/${zone}
		${STAGING_BINDIR_NATIVE}/zic -b ${ZIC_FMT} -d ${B}/zoneinfo/posix -L /dev/null ${S}/${zone}
		${STAGING_BINDIR_NATIVE}/zic -b ${ZIC_FMT} -d ${B}/zoneinfo/right -L ${S}/leapseconds ${S}/${zone}
	done
}

do_install() {
	install -d ${D}${datadir}/zoneinfo
	cp -pPR ${B}/zoneinfo/* ${D}${datadir}/zoneinfo

	# libc is removing zoneinfo files from package
	cp -pP "${S}/zone.tab" ${D}${datadir}/zoneinfo
	cp -pP "${S}/zone1970.tab" ${D}${datadir}/zoneinfo
	cp -pP "${S}/iso3166.tab" ${D}${datadir}/zoneinfo
	cp -pP "${S}/leapseconds" ${D}${datadir}/zoneinfo
	cp -pP "${S}/leap-seconds.list" ${D}${datadir}/zoneinfo
	cp -pP "${S}/tzdata.zi" ${D}${datadir}/zoneinfo

	# Install default timezone
	if [ -e ${D}${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ]; then
		install -d ${D}${sysconfdir}
		if [ "${INSTALL_TIMEZONE_FILE}" = "1" ]; then
			echo ${DEFAULT_TIMEZONE} > ${D}${sysconfdir}/timezone
		fi
		ln -s ${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ${D}${sysconfdir}/localtime
	else
		bberror "DEFAULT_TIMEZONE is set to an invalid value."
		exit 1
	fi

	chown -R root:root ${D}
}

pkg_postinst:${PN}() {
	etc_lt="$D${sysconfdir}/localtime"
	src="$D${sysconfdir}/timezone"

	if [ -e "$src" ]; then
		tz=$(sed -e 's:#.*::' -e 's:[[:space:]]*::g' -e '/^$/d' "$src")
	fi

	if [ ! -z "$tz" -a ! -e "$D${datadir}/zoneinfo/$tz" ]; then
		echo "You have an invalid TIMEZONE setting in $src"
		echo "Your $etc_lt has been reset to Universal; enjoy!"
		tz="Universal"
		echo "Updating $etc_lt with $D${datadir}/zoneinfo/$tz"
		if [ -L "$etc_lt" ]; then
			rm -f "$etc_lt"
		fi
		ln -s "${datadir}/zoneinfo/$tz" "$etc_lt"
	fi
}

# Packages are primarily organized by directory with a major city in most time
# zones in the base package
TZ_PACKAGES = " \
    tzdata-core tzdata-misc tzdata-posix tzdata-right tzdata-africa \
    tzdata-americas tzdata-antarctica tzdata-arctic tzdata-asia \
    tzdata-atlantic tzdata-australia tzdata-europe tzdata-pacific \
"
PACKAGES = "${TZ_PACKAGES} ${PN}"

FILES:tzdata-africa += "${datadir}/zoneinfo/Africa"

FILES:tzdata-americas += " \
    ${datadir}/zoneinfo/America \
    ${datadir}/zoneinfo/US \
    ${datadir}/zoneinfo/Brazil \
    ${datadir}/zoneinfo/Canada \
    ${datadir}/zoneinfo/Mexico \
    ${datadir}/zoneinfo/Chile \
"

FILES:tzdata-antarctica += "${datadir}/zoneinfo/Antarctica"

FILES:tzdata-arctic += "${datadir}/zoneinfo/Arctic"

FILES:tzdata-asia += " \
    ${datadir}/zoneinfo/Asia \
    ${datadir}/zoneinfo/Indian \
    ${datadir}/zoneinfo/Mideast \
"

FILES:tzdata-atlantic += "${datadir}/zoneinfo/Atlantic"

FILES:tzdata-australia += "${datadir}/zoneinfo/Australia"

FILES:tzdata-europe += "${datadir}/zoneinfo/Europe"

FILES:tzdata-pacific += "${datadir}/zoneinfo/Pacific"

FILES:tzdata-posix += "${datadir}/zoneinfo/posix"

FILES:tzdata-right += "${datadir}/zoneinfo/right"

FILES:tzdata-misc += " \
    ${datadir}/zoneinfo/Cuba \
    ${datadir}/zoneinfo/Egypt \
    ${datadir}/zoneinfo/Eire \
    ${datadir}/zoneinfo/Factory \
    ${datadir}/zoneinfo/GB-Eire \
    ${datadir}/zoneinfo/Hongkong \
    ${datadir}/zoneinfo/Iceland \
    ${datadir}/zoneinfo/Iran \
    ${datadir}/zoneinfo/Israel \
    ${datadir}/zoneinfo/Jamaica \
    ${datadir}/zoneinfo/Japan \
    ${datadir}/zoneinfo/Kwajalein \
    ${datadir}/zoneinfo/Libya \
    ${datadir}/zoneinfo/Navajo \
    ${datadir}/zoneinfo/Poland \
    ${datadir}/zoneinfo/Portugal \
    ${datadir}/zoneinfo/Singapore \
    ${datadir}/zoneinfo/Turkey \
"

FILES:tzdata-core += " \
    ${sysconfdir}/localtime \
    ${sysconfdir}/timezone \
    ${datadir}/zoneinfo/leapseconds \
    ${datadir}/zoneinfo/leap-seconds.list \
    ${datadir}/zoneinfo/tzdata.zi \
    ${datadir}/zoneinfo/Pacific/Honolulu \
    ${datadir}/zoneinfo/America/Anchorage \
    ${datadir}/zoneinfo/America/Los_Angeles \
    ${datadir}/zoneinfo/America/Denver \
    ${datadir}/zoneinfo/America/Chicago \
    ${datadir}/zoneinfo/America/New_York \
    ${datadir}/zoneinfo/America/Caracas \
    ${datadir}/zoneinfo/America/Sao_Paulo \
    ${datadir}/zoneinfo/Europe/London \
    ${datadir}/zoneinfo/Europe/Paris \
    ${datadir}/zoneinfo/Africa/Cairo \
    ${datadir}/zoneinfo/Europe/Moscow \
    ${datadir}/zoneinfo/Asia/Dubai \
    ${datadir}/zoneinfo/Asia/Karachi \
    ${datadir}/zoneinfo/Asia/Dhaka \
    ${datadir}/zoneinfo/Asia/Bangkok \
    ${datadir}/zoneinfo/Asia/Hong_Kong \
    ${datadir}/zoneinfo/Asia/Tokyo \
    ${datadir}/zoneinfo/Australia/Darwin \
    ${datadir}/zoneinfo/Australia/Adelaide \
    ${datadir}/zoneinfo/Australia/Brisbane \
    ${datadir}/zoneinfo/Australia/Sydney \
    ${datadir}/zoneinfo/Pacific/Noumea \
    ${datadir}/zoneinfo/CET \
    ${datadir}/zoneinfo/CST6CDT \
    ${datadir}/zoneinfo/EET \
    ${datadir}/zoneinfo/EST \
    ${datadir}/zoneinfo/EST5EDT \
    ${datadir}/zoneinfo/GB \
    ${datadir}/zoneinfo/GMT \
    ${datadir}/zoneinfo/GMT+0 \
    ${datadir}/zoneinfo/GMT-0 \
    ${datadir}/zoneinfo/GMT0 \
    ${datadir}/zoneinfo/Greenwich \
    ${datadir}/zoneinfo/HST \
    ${datadir}/zoneinfo/MET \
    ${datadir}/zoneinfo/MST \
    ${datadir}/zoneinfo/MST7MDT \
    ${datadir}/zoneinfo/NZ \
    ${datadir}/zoneinfo/NZ-CHAT \
    ${datadir}/zoneinfo/PRC \
    ${datadir}/zoneinfo/PST8PDT \
    ${datadir}/zoneinfo/ROC \
    ${datadir}/zoneinfo/ROK \
    ${datadir}/zoneinfo/UCT \
    ${datadir}/zoneinfo/UTC \
    ${datadir}/zoneinfo/Universal \
    ${datadir}/zoneinfo/W-SU \
    ${datadir}/zoneinfo/WET \
    ${datadir}/zoneinfo/Zulu \
    ${datadir}/zoneinfo/zone.tab \
    ${datadir}/zoneinfo/zone1970.tab \
    ${datadir}/zoneinfo/iso3166.tab \
    ${datadir}/zoneinfo/Etc \
"

CONFFILES:tzdata-core = "${sysconfdir}/localtime ${sysconfdir}/timezone"

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} = "${TZ_PACKAGES}"
