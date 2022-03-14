require ${BPN}.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-native:"

inherit meson pkgconfig native

DEPENDS += " \
    meson-native \
    glib-2.0-native \
    lcms-native \
"

SRC_URI += " \
    file://0001-Move-the-function-cd_icc_create_from_edid-to-avoid-u.patch \
    file://Makefile;subdir=${BPN}-${PV} \
"

do_configure() {
    # we expect meson to fail - but before it extracts project's version in log file
    meson ${MESONOPTS} "${MESON_SOURCEPATH}" "${B}" ${MESON_CROSS_FILE} ${EXTRA_OEMESON} > /dev/null 2>&1 || true

    # extract and split version
    version=`grep 'Project version:' ${B}/meson-logs/meson-log.txt | sed 's|Project version: ||'`
    major=`echo $version | cut -d. -f1`
    minor=`echo $version | cut -d. -f2`
    micro=`echo $version | cut -d. -f3`
    echo "Project version: $major.$minor.$micro"

    # extract project name
    proj_name=`grep 'Project name:' ${B}/meson-logs/meson-log.txt | sed 's|Project name: ||'`

    # create cd-version.h
    mkdir -p ${B}/colord
	sed ${S}/lib/colord/cd-version.h.in \
		-e 's:@CD_MAJOR_VERSION_PRIVATE@:1:g' \
		-e 's:@CD_MINOR_VERSION_PRIVATE@:4:g' \
		-e 's:@CD_MICRO_VERSION_PRIVATE@:4:g' \
		> ${B}/colord/cd-version.h

    # create config.h based on target build and add what's necessary only
    localedir=`echo ${datadir}/locale | sed 's:${prefix}/::g'`
    echo "#define LOCALEDIR \"$localedir\"" >> ${B}/config.h
    echo "#define GETTEXT_PACKAGE \"colord\"" >> ${B}/config.h
    echo "#define PACKAGE_NAME \"$proj_name\"" >> ${B}/config.h
    echo "#define PACKAGE_VERSION \"$version\"" >> ${B}/config.h
}

do_compile() {
    oe_runmake -C${S} DESTDIR=${B}
}

do_install() {
    version=`grep 'Project version:' ${B}/meson-logs/meson-log.txt | sed 's|Project version: ||'`
    major=`echo $version | cut -d. -f1`

    install -d ${D}${libdir}
    install -m 755 ${B}/libcolord.so ${D}${libdir}/libcolord.so.$version
    ln -s libcolord.so.$version "${D}/${libdir}/libcolord.so"
    ln -s libcolord.so.$version "${D}/${libdir}/libcolord.so.$major"

    install -d ${D}${bindir}
    install -m 755 ${B}/cd_create_profile ${D}${bindir}/
    install -m 755 ${B}/cd_idt8 ${D}${bindir}/
}
