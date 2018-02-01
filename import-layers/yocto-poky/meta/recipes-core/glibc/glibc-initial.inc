DEPENDS = "linux-libc-headers virtual/${TARGET_PREFIX}gcc-initial libgcc-initial"
PROVIDES = "virtual/${TARGET_PREFIX}libc-initial"

PACKAGES = ""
PACKAGES_DYNAMIC = ""

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${SDK_SYS}"

do_configure () {
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	find ${S} -name "configure" | xargs touch
	cfgscript=`python3 -c "import os; print(os.path.relpath('${S}', '.'))"`/configure
	$cfgscript --host=${TARGET_SYS} --build=${BUILD_SYS} \
		--prefix=/usr \
		--without-cvs --disable-sanity-checks \
		--with-headers=${STAGING_DIR_TARGET}${includedir} \
		--enable-hacker-mode --enable-addons
}

do_compile () {
	:
}

do_install () {
	oe_runmake cross-compiling=yes install_root=${D} \
	includedir='${includedir}' prefix='${prefix}' \
	install-bootstrap-headers=yes install-headers

	oe_runmake csu/subdir_lib
	mkdir -p ${D}${libdir}/
	install -m 644 csu/crt[1in].o ${D}${libdir}

	# Two headers -- stubs.h and features.h -- aren't installed by install-headers,
	# so do them by hand.  We can tolerate an empty stubs.h for the moment.
	# See e.g. http://gcc.gnu.org/ml/gcc/2002-01/msg00900.html
	mkdir -p ${D}${includedir}/gnu/
	touch ${D}${includedir}/gnu/stubs.h
	cp ${S}/include/features.h ${D}${includedir}/features.h

	if [ -e ${B}/bits/stdio_lim.h ]; then
		cp ${B}/bits/stdio_lim.h  ${D}${includedir}/bits/
	fi
}

do_stash_locale() {
	:
}

do_siteconfig () {
	:
}

inherit nopackages

# We really only want this built by things that need it, not any recrdeptask
deltask do_build
