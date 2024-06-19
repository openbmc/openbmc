SUMMARY = "A general-purpose utility library"
DESCRIPTION = "GLib is a general-purpose utility library, which provides many useful data types, macros, type conversions, string utilities, file utilities, a main loop abstraction, and so on."
HOMEPAGE = "https://developer.gnome.org/glib/"

# pcre is under BSD;
# docs/reference/COPYING is with a 'public domain'-like license!
LICENSE = "LGPL-2.1-or-later & BSD-3-Clause & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=41890f71f740302b785c27661123bff5 \
                    file://glib/glib.h;beginline=4;endline=17;md5=72f7cc2847407f65d8981ef112e4e630 \
                    file://LICENSES/LGPL-2.1-or-later.txt;md5=41890f71f740302b785c27661123bff5 \
                    file://gmodule/gmodule.h;beginline=4;endline=17;md5=72f7cc2847407f65d8981ef112e4e630 \
                    file://docs/reference/COPYING;md5=f51a5100c17af6bae00735cd791e1fcc"
BUGTRACKER = "http://bugzilla.gnome.org"
SECTION = "libs"

CVE_PRODUCT = "glib"

BBCLASSEXTEND = "native nativesdk"

DEPENDS = "glib-2.0-native \
           virtual/libintl \
           virtual/libiconv \
           libffi \
           libpcre2 \
           zlib"

PACKAGES += "${PN}-codegen ${PN}-utils"

LEAD_SONAME = "libglib-2.0.*"

inherit meson gettext gi-docgen pkgconfig ptest-gnome upstream-version-is-even bash-completion gio-module-cache manpages gobject-introspection-data

S = "${WORKDIR}/glib-${PV}"

GIDOCGEN_MESON_OPTION = "documentation"

PACKAGECONFIG ??= "libmount \
                   ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'introspection', '', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG:class-native = ""

PACKAGECONFIG[libmount] = "-Dlibmount=enabled,-Dlibmount=disabled,util-linux"
PACKAGECONFIG[manpages] = "-Dman-pages=enabled, -Dman-pages=disabled, python3-docutils-native"
PACKAGECONFIG[libelf] = "-Dlibelf=enabled,-Dlibelf=disabled,elfutils"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false,"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux"
PACKAGECONFIG[introspection] = "-Dintrospection=enabled,-Dintrospection=disabled,gobject-introspection-native gobject-introspection glib-2.0-initial"

EXTRA_OEMESON = "-Ddtrace=false -Dsystemtap=false"
EXTRA_OEMESON:append:class-target = " ${@'-Dgir_dir_prefix=${libdir}' if d.getVar('MULTILIBS') else ''}"

do_configure:prepend() {
	sed -i -e '1s,#!.*,#!${USRBINPATH}/env python3,' ${S}/gio/gdbus-2.0/codegen/gdbus-codegen.in
}

FILES:${PN} = "${libdir}/lib*${SOLIBS} \
               ${libdir}/gio \
               ${libexecdir}/*gio-querymodules \
               ${libexecdir}/*gio-launch-desktop \
               ${datadir}/glib-2.0/dtds \
               ${datadir}/glib-2.0/schemas \
               ${libdir}/girepository-*/*.typelib \
"
FILES:${PN}-dev += "${libdir}/glib-2.0/include \
                    ${libdir}/gio/modules/lib*${SOLIBSDEV} \
                    ${libdir}/gio/modules/*.la \
                    ${datadir}/glib-2.0/gettext/po/Makefile.in.in \
                    ${datadir}/glib-2.0/schemas/gschema.dtd \
                    ${datadir}/glib-2.0/valgrind/glib.supp \
                    ${datadir}/gir-*/*.gir ${libdir}/gir-*/*.gir \
                    ${datadir}/gettext/its"
FILES:${PN}-dbg += "${datadir}/glib-2.0/gdb ${datadir}/gdb"
FILES:${PN}-codegen = "${datadir}/glib-2.0/codegen/*.py \
                       ${bindir}/gdbus-codegen"
FILES:${PN}-utils = "${bindir}/*"

SHAREDMIMEDEP = "shared-mime-info"
SHAREDMIMEDEP:class-native = ""
# When cross compiling for Windows we don't want to include this
SHAREDMIMEDEP:mingw32 = ""

RRECOMMENDS:${PN} += "${SHAREDMIMEDEP}"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
# Valgrind runtime detection works using hand-written assembly, which
# doesn't support mips16e
CPPFLAGS:append:class-target:mips16e = " -DNVALGRIND=1"

# GLib generally requires gettext to be present so for USE_NLS to yes.  For
# native builds as i18n is disabled globally we have to add a gettext-native dependency.
USE_NLS:class-target = "yes"
USE_NLS:class-nativesdk = "yes"
DEPENDS:append:class-native = " gettext-native"

EXEEXT = ""
EXEEXT:mingw32 = ".exe"

do_install:append () {
	if [ -f ${D}${bindir}/gtester-report ]; then
		sed ${D}${bindir}/gtester-report -i -e '1s|^#!.*|#!/usr/bin/env python3|'
	fi

	# Remove some unpackaged files
	rm -rf ${D}${datadir}/glib-2.0/codegen/__pycache__
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyc
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyo

	if [ -e ${D}${libdir}/charset.alias ]; then
		rm -f ${D}${libdir}/charset.alias
	fi

        # Make sure gio-querymodules is unique among multilibs
        if test "x${MLPREFIX}" != "x"; then
                mv ${D}${libexecdir}/gio-querymodules${EXEEXT} ${D}${libexecdir}/${MLPREFIX}gio-querymodules${EXEEXT}
        fi
        # Autotools does this, meson does not
        mkdir -p ${D}${libdir}/gio/modules
}

do_install:append:class-native () {
	# Link gio-querymodules into ${bindir} as otherwise tools like meson won't find it
        ln -rs ${D}${libexecdir}/gio-querymodules ${D}${bindir}
}

do_install:append:class-target () {
	# Tests are only installed on targets, not native builds.  Separating this out
	# keeps glib-2.0-native from depending on DISTRO_FEATURES
	if [ -f ${D}${datadir}/installed-tests/glib/gdbus-serialization.test ]; then
		if ${@bb.utils.contains("DISTRO_FEATURES", "x11", "false", "true", d)}; then
			rm ${D}${datadir}/installed-tests/glib/gdbus-serialization.test
		fi
	fi
	if [ -f ${D}${datadir}/installed-tests/glib/static-link.test ]; then
        if test "x${MLPREFIX}" != "x"; then
                mv ${D}${datadir}/installed-tests/glib/static-link.test ${D}${datadir}/installed-tests/glib/${MLPREFIX}static-link.test
        fi
	fi
        # https://gitlab.gnome.org/GNOME/glib/-/issues/2810
        rm -f ${D}${datadir}/installed-tests/glib/thread-pool-slow.test
}
do_install:append:class-target:libc-musl () {
        # Remove failing tests on musl libc systems, this helps set baseline for musl testing
        # they remain to be rootcaused and fixed but marked known failures here.
        for t in convert.test collate.test gdatetime.test date.test converter-stream.test option-context.test; do
                rm -rf ${D}${datadir}/installed-tests/glib/$t
        done
}
# As we do not build python3 for windows, makes no sense to ship the script that's using it
do_install:append:mingw32() {
        rm -f ${D}${bindir}/gtester-report
}

CODEGEN_PYTHON_RDEPENDS = "python3 python3-packaging python3-xml"
CODEGEN_PYTHON_RDEPENDS:mingw32 = ""

RDEPENDS:${PN}-codegen += "${CODEGEN_PYTHON_RDEPENDS}"

RDEPENDS:${PN}-ptest += "${PN}-utils"

RDEPENDS:${PN}-ptest += "\
            coreutils \
            libgcc \
            dbus \
            desktop-file-utils \
            gnome-desktop-testing \
            tzdata \
            tzdata-americas \
            tzdata-asia \
            tzdata-europe \
            tzdata-posix \
            shared-mime-info \
            ${PN}-locale-ja \
            ${PN}-locale-fr \
            ${PN}-locale-el \
            ${PN}-locale-hr \
            ${PN}-locale-lt \
            ${PN}-locale-pl \
            ${PN}-locale-ru \
            ${PN}-locale-th \
            python3-core \
            python3-modules \
            ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'python3-dbusmock', '', d)} \
            ${PN}-codegen \
            locale-base-de-de \
            locale-base-es-es \
            locale-base-en-gb \
            locale-base-en-us \
            locale-base-fr-fr \
            locale-base-ru-ru \
           "

RDEPENDS:${PN}-ptest:append:libc-glibc = "\
            glibc-gconv-utf-16 \
            glibc-charmap-utf-8 \
            glibc-gconv-cp1255 \
            glibc-charmap-cp1255 \
            glibc-gconv-utf-32 \
            glibc-gconv-utf-7 \
            glibc-gconv-euc-jp \
            glibc-gconv-iso8859-1 \
            glibc-gconv-iso8859-15 \
            glibc-charmap-invariant \
            glibc-localedata-translit-cjk-variants \
            locale-base-lt-lt \
            locale-base-ja-jp.euc-jp \
            locale-base-fa-ir \
            locale-base-hr-hr \
            locale-base-el-gr \
            locale-base-pl-pl \
            locale-base-pl-pl.iso-8859-2 \
            locale-base-tr-tr \
           "

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://run-ptest \
           file://0001-Fix-DATADIRNAME-on-uclibc-Linux.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           file://0001-Do-not-write-bindir-into-pkg-config-files.patch \
           file://0001-meson-Run-atomics-test-on-clang-as-well.patch \
           file://0001-gio-tests-resources.c-comment-out-a-build-host-only-.patch \
           file://skip-timeout.patch \
           file://0001-girepository-introspection-correctly-install-.gir-fi.patch \
           "
SRC_URI:append:class-native = " file://relocate-modules.patch \ 
                                file://0001-meson.build-do-not-enable-pidfd-features-on-native-g.patch \
                              "

SRC_URI[sha256sum] = "b9cfb6f7a5bd5b31238fd5d56df226b2dda5ea37611475bf89f6a0f9400fe8bd"

# Find any meson cross files in FILESPATH that are relevant for the current
# build (using siteinfo) and add them to EXTRA_OEMESON.
inherit siteinfo
def find_meson_cross_files(d):
    if bb.data.inherits_class('native', d):
        return ""

    thisdir = os.path.normpath(d.getVar("THISDIR"))
    import collections
    sitedata = siteinfo_data(d)
    # filename -> found
    files = collections.OrderedDict()
    for path in d.getVar("FILESPATH").split(":"):
        for element in sitedata:
            filename = os.path.normpath(os.path.join(path, "meson.cross.d", element))
            sanitized_path = filename.replace(thisdir, "${THISDIR}")
            if sanitized_path == filename:
                if os.path.exists(filename):
                    bb.error("Cannot add '%s' to --cross-file, because it's not relative to THISDIR '%s' and sstate signature would contain this full path" % (filename, thisdir))
                continue
            files[filename.replace(thisdir, "${THISDIR}")] = os.path.exists(filename)

    items = ["--cross-file=" + k for k,v in files.items() if v]
    d.appendVar("EXTRA_OEMESON", " " + " ".join(items))
    items = ["%s:%s" % (k, "True" if v else "False") for k,v in files.items()]
    d.appendVarFlag("do_configure", "file-checksums", " " + " ".join(items))

python () {
    find_meson_cross_files(d)
}
