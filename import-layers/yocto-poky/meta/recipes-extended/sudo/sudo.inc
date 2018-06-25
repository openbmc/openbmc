SUMMARY = "Provide limited super user privileges to specific users"
DESCRIPTION = "Sudo (superuser do) allows a system administrator to give certain users (or groups of users) the ability to run some (or all) commands as root while logging all commands and arguments."
HOMEPAGE = "http://www.sudo.ws"
BUGTRACKER = "http://www.sudo.ws/bugs/"
SECTION = "admin"
LICENSE = "ISC & BSD & Zlib"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=7765a3d787cb4fed3ccc3c9cee030af9 \
                    file://plugins/sudoers/redblack.c;beginline=1;endline=41;md5=805782a8466975716f8376b2be9aedde \
                    file://lib/util/reallocarray.c;beginline=3;endline=16;md5=85b0905b795d4d58bf2e00635649eec6 \
                    file://lib/util/fnmatch.c;beginline=3;endline=27;md5=67f83ee9bd456557397082f8f1be0efd \
                    file://lib/util/getcwd.c;beginline=5;endline=27;md5=449af4cc57fc7d46f42090608ba3e681 \
                    file://lib/util/glob.c;beginline=6;endline=31;md5=5872733146b9eb0deb79e1f664815b85 \
                    file://lib/util/snprintf.c;beginline=6;endline=34;md5=c82c1b3a5c32e08545c9ec5d71e41e50 \
                    file://include/sudo_queue.h;beginline=5;endline=27;md5=449af4cc57fc7d46f42090608ba3e681 \
                    file://lib/util/inet_pton.c;beginline=3;endline=17;md5=3970ab0518ab79cbd0bafb697f10b33a"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"

CONFFILES_${PN} = "${sysconfdir}/sudoers"

EXTRA_OECONF = "--with-editor=/bin/vi --with-env-editor"

EXTRA_OECONF_append_libc-musl = " --disable-hardening "

# mksigname/mksiglist are used on build host to generate source files
do_compile_prepend () {
	# Remove build host references from sudo_usage.h
	sed -i  \
	    -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's,--build=${BUILD_SYS},,g' \
	    -e 's,--host=${HOST_SYS},,g' \
	    ${B}/src/sudo_usage.h
	oe_runmake SSP_CFLAGS="" SSP_LDFLAGS="" CC="$BUILD_CC" CFLAGS="$BUILD_CFLAGS" CPPFLAGS="$BUILD_CPPFLAGS -I${S}/include -I${S} -I${B}"  -C lib/util mksigname mksiglist
}

# Explicitly create ${localstatedir}/lib before do_install to ensure
# the directory is accessible by all users. Otherwise the mkinstalldirs
# script (from sudo) will recursively create ${localstatedir}/lib/sudo
# and then chmod each directory with 0700 permissions, which isn't what
# we want (i.e, users would not be able to access /var/lib).
do_install_prepend (){
	mkdir -p ${D}/${localstatedir}/lib
}
