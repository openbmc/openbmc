SUMMARY = "Custom Matchbox session files for the Sato environment"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://session;endline=3;md5=f8a5c5b9c279e52dc094d10e11c2be63"

SECTION = "x11"
RDEPENDS:${PN} = "formfactor matchbox-theme-sato matchbox-panel-2 matchbox-desktop matchbox-session gconf"

# This package is architecture specific because the session script is modified
# based on the machine architecture.
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit features_check

# The matchbox-theme-sato requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "file://session \
           file://index.theme"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILES:${PN} += "${datadir}/themes/Sato/index.theme"

do_install() {
	# This is the set of machine features that the script has markers for
	FEATURES="acpi phone"
	SCRIPT="${S}/sedder"
	rm -f $SCRIPT
	touch $SCRIPT
	for FEAT in $FEATURES; do
		if echo ${MACHINE_FEATURES} | awk "/$FEAT/ {exit 1}"; then
			echo "/feature-$FEAT/d" >> $SCRIPT
		fi
	done

	install -D ${S}/index.theme ${D}/${datadir}/themes/Sato/index.theme
	install -d ${D}/${sysconfdir}/matchbox
	sed -f "$SCRIPT" ${S}/session > ${D}/${sysconfdir}/matchbox/session
        chmod +x ${D}/${sysconfdir}/matchbox/session
}

PACKAGE_WRITE_DEPS += "gconf-native"
pkg_postinst:${PN} () {
	set_value() {
		#type, name, value
		gconftool-2 --config-source=xml::$D${sysconfdir}/gconf/gconf.xml.defaults --direct --type $1 --set /desktop/poky/interface/$2 "$3"
	}
	set_value string theme Adwaita
	set_value string matchbox_theme Sato
	set_value string icon_theme Sato
	set_value bool touchscreen true
	set_value string font_name "Sans 9"
}
