inherit qmake_base

DEPENDS += "qt4-tools-native freetype jpeg libpng zlib dbus openssl glib-2.0 sqlite3 tiff icu"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)}"

require qt4_arch.inc

QT_DISTRO_FLAGS ?= "-no-accessibility -no-sm"
QT_DISTRO_FLAGS_linuxstdbase = "-sm"

QT_SQL_DRIVER_FLAGS ?= "-no-sql-ibase -no-sql-mysql -no-sql-psql -no-sql-odbc -no-sql-sqlite2 -plugin-sql-sqlite -system-sqlite"

QT_GLFLAGS ?= ""

  
QT_QT3SUPPORT ?= "-qt3support"
QT_XML ?= "-xmlpatterns"
QT_WEBKIT ?= "-webkit"
QT_PHONON ?= "-no-phonon"
QT_DBUS ?= "-qdbus"
QT_MULTIMEDIA ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', '-pulseaudio', '-no-pulseaudio', d)}"
 
QT_CONFIG_FLAGS += "-release -no-cups -reduce-relocations \
                    -shared -no-nas-sound -no-nis \
                    -system-libjpeg -system-libpng -system-libtiff -system-zlib \
                    -no-pch -stl -glib -icu \
                    -no-rpath -silent \
		    -D USE_QTMULTIMEDIA=1 \
                    ${QT_DBUS} \
                    ${QT_QT3SUPPORT} \
                    ${QT_WEBKIT} \
                    ${QT_PHONON} \
                    ${QT_XML} \
                    ${QT_MULTIMEDIA} \
                    ${QT_SQL_DRIVER_FLAGS} \
                    ${QT_DISTRO_FLAGS} \
                    ${QT_GLFLAGS}"

EXTRA_OEMAKE = "-e"

EXTRA_ENV = 'QMAKE="${STAGING_BINDIR_NATIVE}/qmake2 -after \
             INCPATH+=${STAGING_INCDIR}/freetype2 LIBS+=-L${STAGING_LIBDIR}" \
             QMAKESPEC="${QMAKESPEC}" LINK="${CXX} -Wl,-rpath-link,${STAGING_LIBDIR}" \
             AR="${TARGET_PREFIX}ar cqs" \
             MOC="${STAGING_BINDIR_NATIVE}/moc4" UIC="${STAGING_BINDIR_NATIVE}/uic4" MAKE="make -e"'

export QT_CONF_PATH="${WORKDIR}/qt.conf"

# Library packages
QT_LIB_NAMES = "Qt3Support QtAssistantClient QtCLucene QtCore QtDBus QtDesigner QtDesignerComponents QtGui QtHelp QtNetwork QtOpenGL QtScript QtScriptTools QtSql QtSvg QtTest QtUiTools QtWebKit QtXml QtXmlPatterns phonon QtMultimedia QtOpenVG QtMediaServices QtDeclarative"

QT_EXTRA_LIBS = "pvrQWSWSEGL"

python __anonymous () {
    lib_packages = []
    dev_packages = []
    dbg_packages = []
    staticdev_packages = []
    for name in d.getVar("QT_LIB_NAMES", True).split():
        pkg = d.getVar("QT_BASE_LIB", True) + name.lower().replace("qt", "").replace("_", "-") + "4"
        # NOTE: the headers for QtAssistantClient are different
        incname = name.replace("QtAssistantClient", "QtAssistant")
        d.setVar("FILES_%s" % pkg, "${libdir}/lib%(name)s${QT_LIBINFIX}.so.*" % locals())
        d.setVar("FILES_%s-dev" % pkg, """${libdir}/lib%(name)s${QT_LIBINFIX}.prl
                  ${libdir}/lib%(name)s${QT_LIBINFIX}.la
                  ${libdir}/lib%(name)s${QT_LIBINFIX}.so
                  ${includedir}/${QT_DIR_NAME}/%(incname)s
                  ${libdir}/pkgconfig/%(name)s${QT_LIBINFIX}.pc""" % locals())
        d.setVar("FILES_%s-staticdev" % pkg, "${libdir}/lib%(name)s${QT_LIBINFIX}.a" % locals())
        d.setVar("FILES_%s-dbg" % pkg, "${libdir}/.debug/lib%(name)s${QT_LIBINFIX}.so*" % locals())
        d.setVar("RRECOMMENDS_%s-dbg" % pkg, "${PN}-dbg")
        lib_packages.append(pkg)
        dev_packages.append("%s-dev" % pkg)
        dbg_packages.append("%s-dbg" % pkg)
        staticdev_packages.append("%s-staticdev" % pkg)
    for name in d.getVar("OTHER_PACKAGES", True).split():
        dbg_packages.append("%s-dbg" % name)
        staticdev_packages.append("%s-staticdev" % name)

    for name in d.getVar("QT_EXTRA_LIBS", True).split():
        pkg = d.getVar("QT_BASE_LIB", True) + name.lower().replace("qt", "").replace("_", "-") + "4"
        d.setVar("FILES_%s" % pkg, "${libdir}/lib%(name)s.so.*" % locals())
        d.setVar("FILES_%s-staticdev" % pkg, "${libdir}/lib%(name)s.a" % locals())
        d.setVar("FILES_%s-dev" % pkg, """${libdir}/lib%(name)s.prl
                  ${libdir}/lib%(name)s.la
                  ${libdir}/lib%(name)s.so
                  ${includedir}/${QT_DIR_NAME}/%(incname)s
                  ${libdir}/pkgconfig/%(name)s.pc""" % locals())
        d.setVar("FILES_%s-dbg" % pkg, "${libdir}/.debug/lib%(name)s.so*" % locals())
        d.setVar("RRECOMMENDS_%s-dbg" % pkg, "${PN}-dbg")
        lib_packages.append(pkg)
        dev_packages.append("%s-dev" % pkg)
        dbg_packages.append("%s-dbg" % pkg)
        staticdev_packages.append("%s-staticdev" % pkg)

    d.setVar("LIB_PACKAGES", " ".join(lib_packages))
    d.setVar("DEV_PACKAGES", " ".join(dev_packages))
    d.setVar("DBG_PACKAGES", " ".join(dbg_packages))
    d.setVar("STATICDEV_PACKAGES", " ".join(staticdev_packages))
}

OTHER_PACKAGES = "\
             ${QT_BASE_NAME}-tools \
             ${QT_BASE_NAME}-assistant \
             ${QT_BASE_NAME}-common \
             ${QT_BASE_NAME}-dbus \
             ${QT_BASE_NAME}-demos \
             ${QT_BASE_NAME}-designer \
             ${QT_BASE_NAME}-examples \
             ${QT_BASE_NAME}-linguist \
             ${QT_BASE_NAME}-makeqpf \
             ${QT_BASE_NAME}-mkspecs \
             ${QT_BASE_NAME}-pixeltool \
             ${QT_BASE_NAME}-qmlviewer \
             ${QT_BASE_NAME}-xmlpatterns \
             ${QT_BASE_NAME}-qt3to4 \
             ${QT_BASE_NAME}-qml-plugins"

#We prepend so ${QT_BASE_NAME}-demos-doc comes before ${PN}-doc,so the packaging of FILES_ get done before.
PACKAGES =+ "${QT_BASE_NAME}-demos-doc"
PACKAGES += " \
             ${LIB_PACKAGES} \
             ${DEV_PACKAGES} \
             ${DBG_PACKAGES} \
             ${STATICDEV_PACKAGES} \
             ${OTHER_PACKAGES}"

PACKAGES_DYNAMIC += "^${QT_BASE_NAME}-plugin-.* ^${QT_BASE_NAME}-translation-.* ^${QT_BASE_NAME}-phrasebook-.*"

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}     = ""
FILES_${PN}-dev = "${includedir}/${QT_DIR_NAME}/Qt/*"
FILES_${PN}-dbg = "/usr/src/debug/"
FILES_${QT_BASE_NAME}-demos-doc = "${docdir}/${QT_DIR_NAME}/qch/qt.qch"
RRECOMMENDS_${PN} = "${LIB_PACKAGES} ${OTHER_PACKAGES}"
RRECOMMENDS_${PN}-dev = "${DEV_PACKAGES}"
RRECOMMENDS_${PN}-dbg = "${DBG_PACKAGES}"
RRECOMMENDS_${QT_BASE_LIB}core4_append_libc-glibc = " glibc-gconv-utf-16"
RRECOMMENDS_${QT_BASE_NAME}-demos += " \
            ${QT_BASE_NAME}-examples \
            ${QT_BASE_NAME}-plugin-sqldriver-sqlite \
            ${QT_BASE_NAME}-plugin-imageformat-jpeg \
            ${QT_BASE_NAME}-qml-plugins \
            ${QT_BASE_NAME}-assistant \
            ${QT_BASE_NAME}-demos-doc"
RRECOMMENDS_${QT_BASE_NAME}-examples += " \
            ${QT_BASE_NAME}-plugin-sqldriver-sqlite \
            ${QT_BASE_NAME}-plugin-imageformat-jpeg \
            ${QT_BASE_NAME}-qml-plugins"
RRECOMMENDS_${QT_BASE_NAME}-qmlviewer += " \
            ${QT_BASE_NAME}-qml-plugins"
RRECOMMENDS_${QT_BASE_NAME}-doc += " \
            ${QT_BASE_NAME}-demos-doc"

RPROVIDES_${QT_BASE_NAME}-tools += "qmake2"
RREPLACES_${QT_BASE_NAME}-tools += "qmake2"

FILES_${QT_BASE_NAME}-tools                = "${bindir}/qttracereplay ${bindir}/qdoc* ${bindir}/qmake ${bindir}/moc ${bindir}/uic* ${bindir}/rcc"
FILES_${QT_BASE_NAME}-tools-dbg            = "${bindir}/.debug/qttracereplay ${bindir}/.debug/qdoc* ${bindir}/.debug/qmake ${bindir}/.debug/uic* ${bindir}/.debug/moc ${bindir}/.debug/rcc"
FILES_${QT_BASE_NAME}-assistant            = "${bindir}/*assistant* ${bindir}/qcollectiongenerator ${bindir}/qhelpconverter ${bindir}/qhelpgenerator"
FILES_${QT_BASE_NAME}-assistant-dbg        = "${bindir}/.debug/*assistant* ${bindir}/.debug/qcollectiongenerator ${bindir}/.debug/qhelpconverter ${bindir}/.debug/qhelpgenerator"
FILES_${QT_BASE_NAME}-common               = "${bindir}/qtconfig"
FILES_${QT_BASE_NAME}-common-dbg           = "${bindir}/.debug/qtconfig"
FILES_${QT_BASE_NAME}-dbus                 = "${bindir}/qdbus ${bindir}/qdbusxml2cpp ${bindir}/qdbuscpp2xml ${bindir}/qdbusviewer"
FILES_${QT_BASE_NAME}-dbus-dbg             = "${bindir}/.debug/qdbus ${bindir}/.debug/qdbusxml2cpp ${bindir}/.debug/qdbuscpp2xml ${bindir}/.debug/qdbusviewer"
FILES_${QT_BASE_NAME}-demos                = "${bindir}/qtdemo* ${bindir}/${QT_DIR_NAME}/demos/*"
FILES_${QT_BASE_NAME}-demos-staticdev      = "${bindir}/${QT_DIR_NAME}/demos/shared/libdemo_shared.a"
FILES_${QT_BASE_NAME}-demos-dbg            = "${bindir}/.debug/qtdemo* ${bindir}/${QT_DIR_NAME}/demos/.debug/*  ${bindir}/${QT_DIR_NAME}/demos/*/.debug  ${bindir}/${QT_DIR_NAME}/demos/*/*/.debug  ${bindir}/${QT_DIR_NAME}/demos/*/*/*/.debug"
FILES_${QT_BASE_NAME}-designer             = "${bindir}/*designer*"
FILES_${QT_BASE_NAME}-designer-dbg         = "${bindir}/.debug/*designer*"
FILES_${QT_BASE_NAME}-examples             = "${bindir}/${QT_DIR_NAME}/examples/*"
FILES_${QT_BASE_NAME}-examples-staticdev   = "${bindir}/${QT_DIR_NAME}/examples/tools/plugandpaint/plugins/libpnp_basictools.a"
FILES_${QT_BASE_NAME}-examples-dbg         = "${bindir}/${QT_DIR_NAME}/examples/.debug ${bindir}/${QT_DIR_NAME}/examples/*/.debug ${bindir}/${QT_DIR_NAME}/examples/*/*/.debug ${bindir}/${QT_DIR_NAME}/examples/*/*/*/.debug ${bindir}/${QT_DIR_NAME}/examples/*/*/*/*/.debug ${bindir}/${QT_DIR_NAME}/examples/declarative/*/*/*/*/*/.debug/* ${bindir}/${QT_DIR_NAME}/examples/declarative/*/*/*/*/.debug/*"
FILES_${QT_BASE_NAME}-linguist             = "${bindir}/*linguist* ${bindir}/lrelease ${bindir}/lupdate ${bindir}/lconvert ${bindir}/qm2ts"
FILES_${QT_BASE_NAME}-linguist-dbg         = "${bindir}/.debug/*linguist* ${bindir}/.debug/lrelease ${bindir}/.debug/lupdate ${bindir}/.debug/lconvert ${bindir}/.debug/qm2ts"
FILES_${QT_BASE_NAME}-pixeltool            = "${bindir}/pixeltool"
FILES_${QT_BASE_NAME}-pixeltool-dbg        = "${bindir}/.debug/pixeltool"
FILES_${QT_BASE_NAME}-qt3to4               = "${bindir}/qt3to4 ${datadir}/${QT_DIR_NAME}/q3porting.xml"
FILES_${QT_BASE_NAME}-qt3to4-dbg           = "${bindir}/.debug/qt3to4"
FILES_${QT_BASE_NAME}-qmlviewer            = "${bindir}/qmlviewer"
FILES_${QT_BASE_NAME}-qmlviewer-dbg        = "${bindir}/.debug/qmlviewer"
FILES_${QT_BASE_NAME}-makeqpf              = "${bindir}/makeqpf"
FILES_${QT_BASE_NAME}-makeqpf-dbg          = "${bindir}/.debug/makeqpf"
FILES_${QT_BASE_NAME}-mkspecs              = "${datadir}/${QT_DIR_NAME}/mkspecs/* ${datadir}/${QT_DIR_NAME}/environment-setup"
FILES_${QT_BASE_NAME}-xmlpatterns          = "${bindir}/xmlpatterns*"
FILES_${QT_BASE_NAME}-xmlpatterns-dbg      = "${bindir}/.debug/xmlpatterns*"
FILES_${QT_BASE_NAME}-qml-plugins          = "${libdir}/${QT_DIR_NAME}/imports/* ${libdir}/${QT_DIR_NAME}/plugins/qmltooling/*"
FILES_${QT_BASE_NAME}-qml-plugins-dbg      = "${libdir}/${QT_DIR_NAME}/imports/*/*/*/.debug/* ${libdir}/${QT_DIR_NAME}/imports/*/.debug ${libdir}/${QT_DIR_NAME}/plugins/qmltooling/.debug"
INSANE_SKIP_${MLPREFIX}${QT_BASE_NAME}-examples      += "libdir"
INSANE_SKIP_${MLPREFIX}${QT_BASE_NAME}-examples-dbg  += "libdir"

# License options, to be set by the recipe if different values are needed
QT_LICENSE_FILE ?= ""
QT_LICENSE_FLAGS ?= "-opensource"

do_configure() {
	unset QMAKESPEC
	unset QTDIR

	set_arch
	set_endian

	if [ ! -e bin/qmake ]; then
		ln -sf ${STAGING_BINDIR_NATIVE}/qmake2 bin/qmake
	fi

	if [ ! -e mkspecs/${TARGET_OS}-oe-g++ ]; then
		ln -sf linux-g++ mkspecs/${TARGET_OS}-oe-g++
	fi

	if [ -f mkspecs/common/g++-unix.conf ] ; then
		# mkspecs were refactored for 4.8.0
		cp -f ${WORKDIR}/g++.conf mkspecs/common/g++-unix.conf
	else
		cp -f ${WORKDIR}/g++.conf mkspecs/common/g++.conf
	fi
	cp -f ${WORKDIR}/linux.conf mkspecs/common/

	echo "[Paths]"                                 > $QT_CONF_PATH
	echo "Prefix=${prefix}"                       >> $QT_CONF_PATH
	echo "Documentation=${docdir}/${QT_DIR_NAME}" >> $QT_CONF_PATH
	echo "Headers=${includedir}/${QT_DIR_NAME}"   >> $QT_CONF_PATH
	echo "Libraries=${libdir}"                    >> $QT_CONF_PATH
	echo "Binaries=${bindir}"                     >> $QT_CONF_PATH
	echo "Plugins=${libdir}/${QT_DIR_NAME}/plugins" >> $QT_CONF_PATH
	echo "Imports=${libdir}/${QT_DIR_NAME}/imports" >> $QT_CONF_PATH
	echo "Data=${datadir}/${QT_DIR_NAME}"         >> $QT_CONF_PATH
	echo "Translations=${datadir}/${QT_DIR_NAME}/translations" >> $QT_CONF_PATH
	echo "Settings=${sysconfdir}/${QT_DIR_NAME}"  >> $QT_CONF_PATH
	echo "Examples=${bindir}/${QT_DIR_NAME}/examples" >> $QT_CONF_PATH
	echo "Demos=${bindir}/${QT_DIR_NAME}/demos"   >> $QT_CONF_PATH

	${EXTRA_QMAKE_MUNGE}|| true

	echo yes | QT_LICENSE_FILE="${QT_LICENSE_FILE}" ./configure -v \
			-prefix ${prefix} \
			-bindir ${bindir} \
			-libdir ${libdir} \
			-datadir ${datadir}/${QT_DIR_NAME} \
			-sysconfdir ${sysconfdir}/${QT_DIR_NAME} \
			-docdir ${docdir}/${QT_DIR_NAME} \
			-headerdir ${includedir}/${QT_DIR_NAME} \
			-plugindir ${libdir}/${QT_DIR_NAME}/plugins \
			-importdir ${libdir}/${QT_DIR_NAME}/imports \
			-translationdir ${datadir}/${QT_DIR_NAME}/translations \
			-examplesdir ${bindir}/${QT_DIR_NAME}/examples \
			-demosdir ${bindir}/${QT_DIR_NAME}/demos \
			-platform ${TARGET_OS}-oe-g++ \
			-xplatform ${TARGET_OS}-oe-g++ \
			${QT_ENDIAN} \
			-crossarch ${QT_ARCH} \
			${QT_LICENSE_FLAGS} \
			${QT_CONFIG_FLAGS} -no-fast \
			-L${STAGING_LIBDIR} -I${STAGING_INCDIR} \
			-I${STAGING_INCDIR}/freetype2
}

do_compile() {
    # Fixup missing wsegl header in some SGX SDKs
    if ! [ -e ${STAGING_INCDIR}/wsegl.h ] ; then
        cp src/3rdparty/powervr/wsegl.h src/plugins/gfxdrivers/powervr/QWSWSEGL/
    fi

    unset CFLAGS CXXFLAGS

    oe_runmake ${EXTRA_ENV}

    # Build target qmake
    export QMAKESPEC="${S}/mkspecs/${TARGET_OS}-oe-g++"
    cd ${S}/qmake
    ${OE_QMAKE_QMAKE}
    oe_runmake CC="${CC}" CXX="${CXX}"
    cd ${S}
}

python populate_packages_prepend() {
    translation_dir = d.expand('${datadir}/${QT_DIR_NAME}/translations/')
    translation_name = d.expand('${QT_BASE_NAME}-translation-%s')
    do_split_packages(d, translation_dir, '^(assistant|designer|linguist|qt|qtconfig|qvfb|qtscript)_(.*)\.qm$', translation_name, '${PN} translation for %s', extra_depends='' )
 
    phrasebook_dir = d.expand('${datadir}/${QT_DIR_NAME}/phrasebooks/')
    phrasebook_name = d.expand('${QT_BASE_NAME}-phrasebook-%s')

    if os.path.exists("%s%s" % (d.expand('${D}'), phrasebook_dir)):
        do_split_packages(d, phrasebook_dir, '^(.*)\.qph$', phrasebook_name, '${PN} phrasebook for %s', extra_depends='' )
    else:
        bb.note("The path does not exist:", d.expand('${D}'), phrasebook_dir)
 
    # Package all the plugins and their -dbg version and create a meta package
    def qtopia_split(path, name, glob):
        """
        Split the package into a normal and -dbg package and then add the
        new packages to the meta package.
        """
        plugin_dir = d.expand('${libdir}/${QT_DIR_NAME}/plugins/%s/' % path)
        if not os.path.exists("%s%s" % (d.expand('${D}'), plugin_dir)):
            bb.note("The path does not exist:", d.expand('${D}'), plugin_dir)
            return
 
        plugin_name = d.expand('${QT_BASE_NAME}-plugin-%s-%%s' % name)
        dev_packages = []
        dev_hook = lambda file,pkg,b,c,d:dev_packages.append((file,pkg))
        do_split_packages(d, plugin_dir, glob, plugin_name, '${PN} %s for %%s' % name, extra_depends='', hook=dev_hook)
        # Create a -dbg package as well
        plugin_dir_dbg = d.expand('${libdir}/${QT_DIR_NAME}/plugins/%s/.debug' % path)
        packages = d.getVar('PACKAGES', False)
        for (file,package) in dev_packages:
            packages = "%s %s-dbg" % (packages, package)
            file_name = os.path.join(plugin_dir_dbg, os.path.basename(file))
            d.setVar("FILES_%s-dbg" % package, file_name)
            d.setVar("DESCRIPTION_%s-dbg" % package, "${PN} %s for %s" % (name, package))

        d.setVar('PACKAGES', packages)

    qtopia_split('accessible',    'accessible',    '^libq(.*)\.so$')
    qtopia_split('codecs',        'codec',         '^libq(.*)\.so$')
    qtopia_split('decorations',   'decoration',    '^libqdecoration(.*)\.so$')
    qtopia_split('designer',      'designer',      '^lib(.*)\.so$')
    qtopia_split('gfxdrivers',    'gfxdriver',     '^libq(.*)\.so$')
    qtopia_split('graphicssystems','graphicssystems', '^libq(.*)\.so$')
    qtopia_split('kbddrivers',    'kbddriver',     '^libq(.*)kbddriver\.so$')
    qtopia_split('mousedrivers',  'mousedriver',   '^libq(.*)mousedriver\.so$')
    qtopia_split('iconengines',   'iconengine',    '^libq(.*)\.so$')
    qtopia_split('imageformats',  'imageformat',   '^libq(.*)\.so$')
    qtopia_split('inputmethods',  'inputmethod',   '^libq(.*)\.so$')
    qtopia_split('sqldrivers',    'sqldriver',     '^libq(.*)\.so$')
    qtopia_split('script',        'script',        '^libqtscript(.*)\.so$')
    qtopia_split('styles',        'style',         '^libq(.*)\.so$')
    qtopia_split('phonon_backend','phonon-backend','^libphonon_(.*)\.so$')
    qtopia_split('bearer',        'bearer',        '^libq(.*)bearer\.so$')
}

do_install() {
	oe_runmake install INSTALL_ROOT=${D}

	# Install a proper target version of qmake
	rm ${D}/${bindir}/qmake
	install -m 0755 bin/qmake2 ${D}${bindir}/qmake

	# fix pkgconfig, libtool and prl files
	sed -i -e 's#-L${S}/lib/\?##g' \
			-e 's#-L${STAGING_LIBDIR}/\?##g' \
			-e 's#STAGING_LIBDIR}#libdir}'#g \
			-e 's#-L${libdir}/\?##g' \
			-e s#'$(OE_QMAKE_LIBS_X11)'#"${OE_QMAKE_LIBS_X11}"#g \
			-e 's#" -Wl,-rpath-link,${S}/lib/\?"##g' \
			-e 's#" -Wl,-rpath-link,${libdir}/\?"##g' \
			-e 's#Iin#I${in#g' \
			-e 's#-L\.\./\.\./WebCore/release\s\+-L\.\./\.\./JavaScriptCore/release\s\+-lwebcore##g' \
			-e 's#-ljscore##g' \
			${D}${libdir}/*.la ${D}${libdir}/*.prl ${D}${libdir}/pkgconfig/*.pc

	# fix pkgconfig files
	sed -i -e s#"moc_location=.*$"#"moc_location=${bindir}/moc4"# \
			-e s#"uic_location=.*$"#"uic_location=${bindir}/uic4"# \
			${D}${libdir}/pkgconfig/*.pc
	for name in ${QT_LIB_NAMES}; do
		sed -i -e /Requires/s#"${name}"#"${name}${QT_LIBINFIX}"#g ${D}${libdir}/pkgconfig/*.pc
	done

	# QT abuses $includedir to point to its headers, which breaks pkgconfig sysroot, so manually fix it up here:
	for pc in ${D}${libdir}/pkgconfig/*.pc ; do
		sed -i -e "s:prefix}/include/${QT_DIR_NAME}/$(basename $pc .pc):prefix}/include:" \
				-e 's:IP{:I${:g' $pc
	done

	#Append an E to the qtdemo file
	if [ -n "${QT_LIBINFIX}" ] ; then
		[ -f ${D}${bindir}/qtdemo ] && mv ${D}${bindir}/qtdemo ${D}${bindir}/qtdemo${QT_LIBINFIX}
	fi

	script="${D}/${datadir}/${QT_DIR_NAME}/environment-setup"
	touch $script
	echo 'export QT_DIR_NAME=${QT_DIR_NAME}' >> $script
	echo 'export QT_LIBINFIX=${QT_LIBINFIX}' >> $script
	echo 'export OE_QMAKE_AR=ar' >> $script
	echo 'export OE_QMAKE_CC=gcc' >> $script
	echo 'export OE_QMAKE_CXX=g++' >> $script
	echo 'export OE_QMAKE_LINK=g++' >> $script
	echo 'export OE_QMAKE_LIBDIR_QT=${libdir}' >> $script
	echo 'export OE_QMAKE_INCDIR_QT=${includedir}/${QT_DIR_NAME}' >> $script
	echo 'export OE_QMAKE_MOC=${bindir}/moc' >> $script
	echo 'export OE_QMAKE_UIC=${bindir}/uic' >> $script
	echo 'export OE_QMAKE_UIC3=${bindir}/uic3' >> $script
	echo 'export OE_QMAKE_RCC=${bindir}/rcc' >> $script
	echo 'export OE_QMAKE_QDBUSCPP2XML=${bindir}/qdbuscpp2xml' >> $script
	echo 'export OE_QMAKE_QDBUSXML2CPP=${bindir}/qdbusxml2cpp' >> $script
	echo 'export OE_QMAKE_QT_CONFIG=${datadir}/${QT_DIR_NAME}/mkspecs/qconfig.pri' >> $script
	echo 'export QMAKESPEC=${datadir}/${QT_DIR_NAME}/mkspecs/linux-g++' >> $script
	chmod 0755 $script
}
