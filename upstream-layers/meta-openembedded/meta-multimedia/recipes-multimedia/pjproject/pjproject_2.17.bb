SUMMARY = "Open source SIP and media stack, plus pjsua2 Python bindings"
DESCRIPTION = "PJSIP is a free and open source multimedia communication library \
written in C with high level API in C, C++, Java, C#, and Python languages. It \
implements standard based protocols such as SIP, SDP, RTP, STUN, TURN, and ICE. \
It combines signaling protocol (SIP) with rich multimedia framework and NAT \
traversal functionality into high level API that is portable and suitable for \
almost any type of systems ranging from desktops, embedded systems, to mobile \
handsets."
HOMEPAGE = "https://www.pjsip.org/"
SECTION = "libs"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/pjsip/pjproject.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "5a457451fa2712ba18e12b01738e8ff3af2b26fd"

DEPENDS = "swig-native python3-setuptools-native"

inherit pkgconfig python3native python3targetconfig

# Not autotools: ./configure is a one-line wrapper round pjproject's own
# aconfigure, which may or may not be autotools... there appears to be no
# documentation.
B = "${S}"

EXTRA_OECONF = "${PACKAGECONFIG_CONFARGS} ${@ '' if d.getVar('DISABLE_STATIC') == '' else '--enable-shared' }"

do_configure() {
	./configure --host=${HOST_SYS} --prefix=${prefix} --libdir=${libdir} --includedir=${includedir} \
		${EXTRA_OECONF}
}

# ssl and sound are the only features enabled by default - everything video-
# related cascades off of --disable-video (aconfigure.ac turns off sdl,
# ffmpeg, v4l2, openh264 and vpx together whenever video itself is off).
PACKAGECONFIG ?= "ssl ${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"

# --with-X=DIR options (ssl, sdl, openh264, vpx) fall back to disabled when
# cross-compiling unless a prefix is explicitly given, so their "on" arg
# always needs an explicit STAGING_DIR_HOST path, not just a bare
# --enable-X/--with-X.
PACKAGECONFIG[alsa] = "--enable-sound,--disable-sound,alsa-lib"
PACKAGECONFIG[ffmpeg] = "--enable-ffmpeg,--disable-ffmpeg,ffmpeg"
PACKAGECONFIG[libwebrtc] = "--enable-libwebrtc,--disable-libwebrtc"
PACKAGECONFIG[openh264] = "--with-openh264=${STAGING_DIR_HOST}${prefix},--disable-openh264,openh264"
PACKAGECONFIG[sdl] = "--with-sdl=${STAGING_DIR_HOST}${prefix},--disable-sdl,libsdl2"
PACKAGECONFIG[ssl] = "--with-ssl=${STAGING_DIR_HOST}${prefix},--without-ssl,openssl"
PACKAGECONFIG[v4l2] = "--enable-v4l2,--disable-v4l2,v4l-utils"
PACKAGECONFIG[video] = "--enable-video,--disable-video"
PACKAGECONFIG[vpx] = "--with-vpx=${STAGING_DIR_HOST}${prefix},--disable-vpx,libvpx"

# pjproject's per-module rules.mak links some binaries and shared libs
# (pjlib-test, libpj.so, etc.) directly via $(LD), not $(CC)/$(CXX).
EXTRA_OEMAKE = "LD='${CXX}'"

# Conservative: pjproject's per-module Makefiles don't always express
# inter-file build ordering correctly for -j. Revisit if a parallel build
# turns out to be reliable.
PARALLEL_MAKE = ""

do_compile:prepend() {
	oe_runmake dep
}

PYSWIG_DIR = "${S}/pjsip-apps/src/swig/python"

do_compile:append() {
	# Reuse pjproject's own SWIG python Makefile (handles the swig
	# codegen step with the right -I flags) rather than reimplementing
	# it, just pointing PYTHON_EXE at the correctly cross-configured
	# native python3 (python3native/python3targetconfig, above) instead
	# of its own default bare "python3" off PATH.
	cd ${PYSWIG_DIR}
	oe_runmake PYTHON_EXE=${STAGING_BINDIR_NATIVE}/python3-native/python3
	cd -
}

do_install() {
	# pjproject's own "install:" target does DESTDIR-aware "cp -af
	# $(APP_LIBXX_FILES) $(DESTDIR)$(libdir)/" etc for the C/C++ libraries,
	# headers and libpjproject.pc - this is what autotools_do_install used
	# to provide for free; now explicit since this isn't an autotools
	# recipe.
	oe_runmake 'DESTDIR=${D}' install

	# -a preserves the *source* files' ownership (real host-user, since
	# do_compile doesn't run under pseudo) instead of a fresh pseudo-
	# tracked "create as root" event, which later trips up sstate
	# signature computation ("uid not found", host contamination). Force
	# every installed file through a real chown so pseudo re-registers
	# correct ownership.
	chown -R root:root ${D}

	# Not "make install" (upstream's own install target here does
	# "setup.py install --user", not DESTDIR-aware) - invoke setup.py
	# directly, same pattern as setuptools3_legacy_do_install.
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cd ${PYSWIG_DIR}
	STAGING_INCDIR=${STAGING_INCDIR} \
	STAGING_LIBDIR=${STAGING_LIBDIR} \
	PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR}:$PYTHONPATH \
	${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py \
	install --skip-build --root=${D} --prefix=${prefix} --install-lib=${PYTHON_SITEPACKAGES_DIR}
}

PACKAGES =+ "python3-pjsua2"
FILES:python3-pjsua2 = "\
    ${PYTHON_SITEPACKAGES_DIR}/pjsua2* \
    ${PYTHON_SITEPACKAGES_DIR}/_pjsua2* \
    ${PYTHON_SITEPACKAGES_DIR}/__pycache__ \
"
RDEPENDS:python3-pjsua2 = "python3-core"

# Static libs only: everything lands in ${PN}-dev (headers, libpjproject.pc)
# and ${PN}-staticdev (the .a files) by bitbake's own default FILES
# patterns - ${PN} itself ships nothing.
ALLOW_EMPTY:${PN} = "1"
