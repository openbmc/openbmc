SUMMARY = "Qt version 4 tools and support files for the build host"
DEPENDS = "zlib-native dbus-native"
SECTION = "libs"
HOMEPAGE = "http://qt-project.org/"
PROVIDES = "qt4-tools-native"

LICENSE = "LGPLv2.1 | GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://LICENSE.GPL3;md5=6e1694ee338db410417517884918d4d2 \
                    file://LGPL_EXCEPTION.txt;md5=0145c4d1b6f96a661c2c139dfb268fb6"

inherit native

SRC_URI = "http://download.qt-project.org/official_releases/qt/4.8/${PV}/qt-everywhere-opensource-src-${PV}.tar.gz \
           file://0001-qlibraryinfo-allow-to-set-qt.conf-from-the-outside-u.patch \
           file://0002-qkbdtty_qws-fix-build-with-old-kernel-headers.patch \
           file://0003-webkit2-set-OUTPUT_DIR-value-if-empty.patch \
           file://0021-configure-make-qt4-native-work-with-long-building-pa.patch \
           file://g++.conf \
           file://linux.conf \
	"
S = "${WORKDIR}/qt-everywhere-opensource-src-${PV}"

EXTRA_OECONF = "-prefix ${prefix} \
                -bindir ${bindir} \
                -libdir ${libdir} \
                -datadir ${datadir}/qt4 \
                -sysconfdir ${sysconfdir}/qt4 \
                -docdir ${docdir}/qt4 \
                -headerdir ${includedir}/qt4 \
                -plugindir ${libdir}/qt4/plugins \
                -importdir ${libdir}/qt4/imports \
                -translationdir ${datadir}/qt4/translations \
                -examplesdir ${bindir}/qt4/examples \
                -demosdir ${bindir}/qt4/demos \
                -L ${STAGING_LIBDIR_NATIVE} \
                -I ${STAGING_INCDIR_NATIVE} \
                -qt-libjpeg -system-zlib \
                -no-libjpeg -no-libpng -no-libmng -no-libtiff \
                -no-accessibility \
                -no-cups \
                -no-nas-sound \
                -no-nis -no-openssl \
                -verbose -release \
                -embedded -no-freetype -no-glib -no-iconv \
                -exceptions -xmlpatterns \
                -qt3support \
                -no-fast -silent -no-rpath"

# yank default -e, otherwise we get the following error:
# moc_qbuffer.cpp: No such file or directory
EXTRA_OEMAKE = " "

do_configure() {
	# Avoid problems with Qt 4.8.0 configure setting QMAKE_LINK from LD (since we want the linker to be g++)
	unset LD

	(echo o; echo yes) | ./configure ${EXTRA_OECONF} || die "Configuring qt failed. EXTRA_OECONF was ${EXTRA_OECONF}"
}

TOBUILD = "\
  src/tools/moc \
  src/corelib \
  src/sql \
  src/xml \
  src/network \
  src/tools/uic \
  src/tools/rcc \
  src/xmlpatterns \
  src/dbus \
  src/gui \
  src/testlib \
  src/qt3support \
  src/tools/uic3 \
  tools/linguist/lconvert \
  tools/linguist/lrelease \
  tools/linguist/lupdate \
  tools/qdbus/qdbuscpp2xml \
  tools/qdbus/qdbusxml2cpp \
  tools/xmlpatterns \
"

do_compile() {
	for i in ${TOBUILD}; do
		cd ${S}/$i && oe_runmake CC="${CC}" CXX="${CXX}"
	done
}

do_install() {
	install -d ${D}${bindir}/
	install -m 0755 bin/qmake ${D}${bindir}/qmake2
	for i in moc uic uic3 rcc lconvert lrelease lupdate qdbuscpp2xml qdbusxml2cpp xmlpatterns; do
		install -m 0755 bin/${i} ${D}${bindir}/${i}4
	done
    
	install -d ${D}${datadir}/qt4/
	cp -PfR mkspecs ${D}${datadir}/qt4/
	ln -sf linux-g++ ${D}${datadir}/qt4/mkspecs/${BUILD_OS}-oe-g++
	if [ -f ${D}${datadir}/qt4/mkspecs/common/g++-unix.conf ] ; then
		# mkspecs were refactored for 4.8.0
		cp -f ${WORKDIR}/g++.conf ${D}${datadir}/qt4/mkspecs/common/g++-unix.conf
	else
		cp -f ${WORKDIR}/g++.conf ${D}${datadir}/qt4/mkspecs/common/g++.conf
	fi
	cp -f ${WORKDIR}/linux.conf ${D}${datadir}/qt4/mkspecs/common/

	install -m 0644 tools/porting/src/q3porting.xml ${D}${datadir}/qt4/

	for i in ${TOBUILD}; do
		cd ${S}/$i && oe_runmake install INSTALL_ROOT=${D}
	done
}
