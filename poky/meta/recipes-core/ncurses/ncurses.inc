SUMMARY = "The New Curses library"
DESCRIPTION = "SVr4 and XSI-Curses compatible curses library and terminfo tools including tic, infocmp, captoinfo. Supports color, multiple highlights, forms-drawing characters, and automatic recognition of keypad and function-key sequences. Extensions include resizable windows and mouse support on both xterm and Linux console using the gpm library."
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ncurses/base/version.c;beginline=1;endline=27;md5=5526f2f3a29edc95538b368a4771edda"
SECTION = "libs"
DEPENDS = "ncurses-native"
DEPENDS_class-native = ""

BINCONFIG = "${bindir}/ncurses5-config ${bindir}/ncursesw5-config \
	${bindir}/ncurses6-config ${bindir}/ncursesw6-config"

inherit autotools binconfig-disabled multilib_header pkgconfig

# Upstream has useful patches at times at ftp://invisible-island.net/ncurses/
SRC_URI = "git://salsa.debian.org/debian/ncurses.git;protocol=https"

EXTRA_AUTORECONF = "-I m4"
CONFIG_SITE =+ "${WORKDIR}/config.cache"

EXTRASITECONFIG = "CFLAGS='${CFLAGS} -I${SYSROOT_DESTDIR}${includedir}'"

# Whether to enable separate widec libraries; must be 'true' or 'false'
#
# TODO: remove this variable when widec is supported in every setup?
ENABLE_WIDEC ?= "true"

# _GNU_SOURCE is required for widec stuff and is detected automatically
# for target objects.  But it must be set manually for native and sdk
# builds.
BUILD_CPPFLAGS += "-D_GNU_SOURCE"

# natives don't generally look in base_libdir
base_libdir_class-native = "${libdir}"

# Display corruption occurs on 64 bit hosts without these settings
# This was derrived from the upstream debian ncurses which uses
# these settings for 32 and 64 bit hosts.
EXCONFIG_ARGS = ""
EXCONFIG_ARGS_class-native = " \
		--disable-lp64 \
		--with-chtype='long' \
		--with-mmask-t='long'"
EXCONFIG_ARGS_class-nativesdk = " \
		--disable-lp64 \
		--with-chtype='long' \
		--with-mmask-t='long'"

PACKAGES_DYNAMIC = "^${PN}-lib.*"

# Fall back to the host termcap / terminfo for -nativesdk and -native
# The reality is a work around for strange problems with things like
# "bitbake -c menuconfig busybox" where it cannot find the terminfo
# because the sstate had a hard coded search path.  Until this is fixed
# another way this is deemed good enough.
EX_TERMCAP = ""
EX_TERMCAP_class-native = ":/etc/termcap:/usr/share/misc/termcap"
EX_TERMCAP_class-nativesdk = ":/etc/termcap:/usr/share/misc/termcap"
EX_TERMINFO = ""
EX_TERMINFO_class-native = ":/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo"
EX_TERMINFO_class-nativesdk = ":/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo"
EX_TERMLIB ?= "tinfo"

# Helper function for do_configure to allow multiple configurations
# $1 the directory to run configure in
# $@ the arguments to pass to configure
ncurses_configure() {
	mkdir -p $1
	cd $1
	shift
	oe_runconf \
	        --without-debug \
	        --without-ada \
	        --without-gpm \
	        --enable-hard-tabs \
	        --enable-xmc-glitch \
	        --enable-colorfgbg \
	        --with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap${EX_TERMCAP}' \
	        --with-terminfo-dirs='${sysconfdir}/terminfo:${datadir}/terminfo${EX_TERMINFO}' \
	        --with-shared \
	        --disable-big-core \
	        --program-prefix= \
	        --with-ticlib \
	        --with-termlib=${EX_TERMLIB} \
	        --enable-sigwinch \
	        --enable-pc-files \
	        --disable-rpath-hack \
		${EXCONFIG_ARGS} \
	        --with-manpage-format=normal \
	        --without-manpage-renames \
	        --disable-stripping \
	        "$@" || return 1
	cd ..
}

# Override the function from the autotools class; ncurses requires a
# patched autoconf213 to generate the configure script. This autoconf
# is not available so that the shipped script will be used.
do_configure() {
        # check does not work with cross-compiling and is generally
        # broken because it requires stdin to be pollable (which is
        # not the case for /dev/null redirections)
        export cf_cv_working_poll=yes
	#Remove ${includedir} from CPPFLAGS, need for cross compile
	sed -i 's#-I${cf_includedir}##g' ${S}/configure || die "sed CPPFLAGS"

	# The --enable-pc-files requires PKG_CONFIG_LIBDIR existed
	mkdir -p ${PKG_CONFIG_LIBDIR}
	( cd ${S}; gnu-configize --force )
	ncurses_configure "narrowc" || \
		return 1
	! ${ENABLE_WIDEC} || \
		ncurses_configure "widec" "--enable-widec" "--without-progs"

}

do_compile() {
        oe_runmake -C narrowc libs
        oe_runmake -C narrowc/progs

        ! ${ENABLE_WIDEC} || \
            oe_runmake -C widec libs
}

# set of expected differences between narrowc and widec header
#
# TODO: the NCURSES_CH_T difference can cause real problems :(
_unifdef_cleanup = " \
  -e '\!/\* \$Id: curses.wide,v!,\!/\* \$Id: curses.tail,v!d' \
  -e '/^#define NCURSES_CH_T /d' \
  -e '/^#include <wchar.h>/d' \
  -e '\!^/\* .* \*/!d' \
"

do_test[depends] = "unifdef-native:do_populate_sysroot"
do_test[dirs] = "${S}"
do_test() {
        ${ENABLE_WIDEC} || return 0

        # make sure that the narrow and widec header are compatible
        # and differ only in minor details.
        unifdef -k narrowc/include/curses.h | \
            sed ${_unifdef_cleanup} > curses-narrowc.h
        unifdef -k widec/include/curses.h | \
            sed ${_unifdef_cleanup} > curses-widec.h

        diff curses-narrowc.h curses-widec.h
}

# Split original _install_opts to two parts.
# One is the options to install contents, the other is the parameters \
# when running command "make install"
# Note that install.libs will also implicitly install header files,
# so we do not need to explicitly specify install.includes.
# Doing so could in fact result in a race condition, as both targets
# (install.libs and install.includes) would install the same headers
# at the same time

_install_opts = " install.libs install.man "

_install_cfgs = "\
  DESTDIR='${D}' \
  PKG_CONFIG_LIBDIR='${libdir}/pkgconfig' \
"

do_install() {
        # Order of installation is important; widec installs a 'curses.h'
        # header with more definitions and must be installed last hence.
        # Compatibility of these headers will be checked in 'do_test()'.
        oe_runmake -C narrowc ${_install_cfgs} ${_install_opts} \
                install.progs

        # The install.data should run after install.libs, otherwise
        # there would be a race issue in a very critical conditon, since
        # tic will be run by install.data, and tic needs libtinfo.so
        # which would be regenerated by install.libs.
        oe_runmake -C narrowc ${_install_cfgs} \
                install.data


        ! ${ENABLE_WIDEC} || \
            oe_runmake -C widec ${_install_cfgs} ${_install_opts}

        cd narrowc

        # include some basic terminfo files
        # stolen ;) from gentoo and modified a bit
        for x in ansi console dumb linux rxvt screen screen-256color sun vt52 vt100 vt102 vt200 vt220 xterm-color xterm-xfree86 xterm-256color
        do
                local termfile="$(find "${D}${datadir}/terminfo/" -name "${x}" 2>/dev/null)"
                local basedir="$(basename $(dirname "${termfile}"))"

                if [ -n "${termfile}" ]
                then
                        install -d ${D}${sysconfdir}/terminfo/${basedir}
                        mv ${termfile} ${D}${sysconfdir}/terminfo/${basedir}/
                        ln -s /etc/terminfo/${basedir}/${x} \
                                ${D}${datadir}/terminfo/${basedir}/${x}
                fi
        done
        # i think we can use xterm-color as default xterm
        if [ -e ${D}${sysconfdir}/terminfo/x/xterm-color ]
        then
                ln -sf xterm-color ${D}${sysconfdir}/terminfo/x/xterm
        fi

        # When changing ${libdir} to e.g. /usr/lib/myawesomelib/ ncurses 
        # still installs '/usr/lib/terminfo', so try to rm both 
        # the proper path and a slightly hardcoded one
        rm -f ${D}${libdir}/terminfo ${D}${prefix}/lib/terminfo

        # create linker scripts for libcurses.so and libncurses to
        # link against -ltinfo when needed. Some builds might break
        # else when '-Wl,--no-copy-dt-needed-entries' has been set in
        # linker flags.
        for i in libncurses libncursesw; do
                f=${D}${libdir}/$i.so
                test -h $f || continue
                rm -f $f
                echo '/* GNU ld script */'  >$f
                echo "INPUT($i.so.5 AS_NEEDED(-ltinfo))" >>$f
        done

        # Make sure that libcurses is linked so that it gets -ltinfo
        # also, this should be addressed upstream really.
        ln -sf libncurses.so ${D}${libdir}/libcurses.so

        # create libtermcap.so linker script for backward compatibility
        f=${D}${libdir}/libtermcap.so
        echo '/* GNU ld script */' >$f
        echo 'INPUT(AS_NEEDED(-ltinfo))' >>$f

        if [ ! -d "${D}${base_libdir}" ]; then
            # Setting base_libdir to libdir as is done in the -native
            # case will skip this code
            mkdir -p ${D}${base_libdir}
            mv ${D}${libdir}/libncurses.so.* ${D}${base_libdir}
            ! ${ENABLE_WIDEC} || \
                mv ${D}${libdir}/libncursesw.so.* ${D}${base_libdir}

            mv ${D}${libdir}/libtinfo.so.* ${D}${base_libdir}
            rm ${D}${libdir}/libtinfo.so

            # Use lnr to ensure this is a relative link despite absolute paths
            # (as we can't know the relationship between base_libdir and libdir).
            # At some point we can rely on coreutils 8.16 which has ln -r.
            lnr ${D}${base_libdir}/libtinfo.so.5 ${D}${libdir}/libtinfo.so
        fi
        if [ -d "${D}${includedir}/ncurses" ]; then
            for f in `find ${D}${includedir}/ncurses -name "*.h"`
            do
	        f=`basename $f`
	        test -e ${D}${includedir}/$f && continue
                ln -sf ncurses/$f ${D}${includedir}/$f
            done
        fi
        oe_multilib_header curses.h
}

python populate_packages_prepend () {
    libdir = d.expand("${libdir}")
    base_libdir = d.expand("${base_libdir}")
    pnbase = d.expand("${PN}-lib%s")
    do_split_packages(d, libdir, r'^lib(.*)\.so\..*', pnbase, 'ncurses %s library', prepend=True, extra_depends = '', allow_links=True)
    if libdir is not base_libdir:
        do_split_packages(d, base_libdir, r'^lib(.*)\.so\..*', pnbase, 'ncurses %s library', prepend=True, extra_depends = '', allow_links=True)
}


inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_ncurses-tools_class-target = "clear reset"

BBCLASSEXTEND = "native nativesdk"

PACKAGES += " \
  ${PN}-tools \
  ${PN}-terminfo-base \
  ${PN}-terminfo \
"

FILES_${PN} = "\
  ${bindir}/tput \
  ${bindir}/tset \
  ${bindir}/ncurses5-config \
  ${bindir}/ncursesw5-config \
  ${bindir}/ncurses6-config \
  ${bindir}/ncursesw6-config \
  ${datadir}/tabset \
"

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_${PN}-tools = "\
  ${bindir}/tic \
  ${bindir}/toe \
  ${bindir}/infotocap \
  ${bindir}/captoinfo \
  ${bindir}/infocmp \
  ${bindir}/clear${@['', '.${BPN}']['${CLASSOVERRIDE}' == 'class-target']} \
  ${bindir}/reset${@['', '.${BPN}']['${CLASSOVERRIDE}' == 'class-target']} \
  ${bindir}/tack \
  ${bindir}/tabs \
"

# 'reset' is a symlink to 'tset' which is in the 'ncurses' package
RDEPENDS_${PN}-tools = "${PN}"

FILES_${PN}-terminfo = "\
  ${datadir}/terminfo \
"

FILES_${PN}-terminfo-base = "\
  ${sysconfdir}/terminfo \
"

RSUGGESTS_${PN}-libtinfo = "${PN}-terminfo"
RRECOMMENDS_${PN}-libtinfo = "${PN}-terminfo-base"
