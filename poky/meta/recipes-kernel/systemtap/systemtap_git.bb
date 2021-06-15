SUMMARY = "Script-directed dynamic tracing and performance analysis tool for Linux"
DESCRIPTION = "It provides free software infrastructure to simplify the \
gathering of information about the running Linux system. This assists \
diagnosis of a performance or functional problem."
HOMEPAGE = "https://sourceware.org/systemtap/"

require systemtap_git.inc

SRC_URI += "file://0001-improve-reproducibility-for-c-compiling.patch"

DEPENDS = "elfutils"

EXTRA_OECONF += "--with-libelf=${STAGING_DIR_TARGET} --without-rpm \
            --without-nss --without-avahi --without-dyninst \
            --disable-server --disable-grapher --enable-prologues \
            --with-python3 --without-python2-probes \
            ac_cv_prog_have_javac=no \
            ac_cv_prog_have_jar=no "

STAP_DOCS ?= "--disable-docs --disable-publican --disable-refdocs"

EXTRA_OECONF += "${STAP_DOCS} "

PACKAGECONFIG ??= "translator sqlite monitor python3-probes"
PACKAGECONFIG[translator] = "--enable-translator,--disable-translator,boost,bash"
PACKAGECONFIG[libvirt] = "--enable-libvirt,--disable-libvirt,libvirt"
PACKAGECONFIG[sqlite] = "--enable-sqlite,--disable-sqlite,sqlite3"
PACKAGECONFIG[monitor] = "--enable-monitor,--disable-monitor,ncurses json-c"
PACKAGECONFIG[python3-probes] = "--with-python3-probes,--without-python3-probes,python3-setuptools-native"

inherit autotools gettext pkgconfig systemd
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3-probes', 'distutils3-base', '', d)}

# exporter comes with python3-probes
PACKAGES =+ "${PN}-exporter"
FILES_${PN}-exporter = "${sysconfdir}/stap-exporter/* \
                        ${sysconfdir}/sysconfig/stap-exporter \
                        ${systemd_unitdir}/system/stap-exporter.service \
                        ${sbindir}/stap-exporter"
RDEPENDS_${PN}-exporter = "${PN} python3-core python3-netclient"
SYSTEMD_SERVICE_${PN}-exporter = "stap-exporter.service"

PACKAGES =+ "${PN}-runtime"
FILES_${PN}-runtime = "\
 ${bindir}/staprun \
 ${bindir}/stap-merge \
 ${bindir}/stapsh \
 ${libexecdir}/${BPN}/stapio \
"
RDEPENDS_${PN}_class-target += "${PN}-runtime"

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${datadir}/${BPN}/examples/"
RDEPENDS_${PN}-examples += "${PN}"

# don't complain that some examples involve bash, perl, php...
INSANE_SKIP_${PN}-examples += "file-rdeps"

PACKAGES =+ "${PN}-python"
FILES_${PN}-python += "\
 ${bindir}/dtrace \
 ${libdir}/python*/ \
 ${libexecdir}/${BPN}/python/ \
"
# python material requires sdt headers
RDEPENDS_${PN}-python += "${PN}-dev python3-core"
INSANE_SKIP_${PN}-python += "dev-deps"

do_configure_prepend () {
    # Improve reproducibility for c++ object files
    reltivepath="${@os.path.relpath(d.getVar('STAGING_INCDIR'), d.getVar('S'))}"
    sed -i "s:@RELATIVE_STAGING_INCDIR@:$reltivepath:g" ${S}/stringtable.h
}

do_install_append () {
   if [ ! -f ${D}${bindir}/stap ]; then
      # translator disabled case, need to leave only minimal runtime
      rm -rf ${D}${datadir}/${PN}
      rm ${D}${libexecdir}/${PN}/stap-env
   fi

   if [ -d ${D}${prefix}/lib/systemd -a ${D}${prefix}/lib != `dirname ${D}${systemd_unitdir}` ]; then
      # Fix makefile hardcoded path assumptions for systemd (assumes $prefix)
      # without usrmerge distro feature enabled
      install -d `dirname ${D}${systemd_unitdir}`
      mv ${D}${prefix}/lib/systemd `dirname ${D}${systemd_unitdir}`
      rmdir ${D}${prefix}/lib --ignore-fail-on-non-empty
   fi

   # Ensure correct ownership for files copied in
   if [ -d ${D}${sysconfdir}/stap-exporter ]; then
       chown root:root ${D}${sysconfdir}/stap-exporter/* -R
   fi
}

BBCLASSEXTEND = "nativesdk"
