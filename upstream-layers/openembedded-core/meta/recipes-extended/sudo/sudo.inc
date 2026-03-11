SUMMARY = "Provide limited super user privileges to specific users"
DESCRIPTION = "Sudo (superuser do) allows a system administrator to give certain users (or groups of users) the ability to run some (or all) commands as root while logging all commands and arguments."
HOMEPAGE = "http://www.sudo.ws"
BUGTRACKER = "http://www.sudo.ws/bugs/"
SECTION = "admin"
LICENSE = "ISC & BSD-3-Clause & BSD-2-Clause & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2841c822e587db145364ca95e9be2ffa \
                    file://plugins/sudoers/redblack.c;beginline=1;endline=46;md5=03e35317699ba00b496251e0dfe9f109 \
                    file://lib/util/reallocarray.c;beginline=3;endline=15;md5=397dd45c7683e90b9f8bf24638cf03bf \
                    file://lib/util/fnmatch.c;beginline=3;endline=27;md5=004d7d2866ba1f5b41174906849d2e0f \
                    file://lib/util/glob.c;beginline=2;endline=31;md5=2852f68687544e3eb8a0a61665506f0e \
                    file://lib/util/snprintf.c;beginline=3;endline=33;md5=b70df6179969e38fcf68da91b53b8029 \
                    file://include/sudo_queue.h;beginline=2;endline=27;md5=ad578e9664d17a010b63e4bc0576ee8d \
                    file://lib/util/inet_pton.c;beginline=3;endline=17;md5=27785c9f5835093eda42aa0816a2d0b4 \
                    file://lib/util/arc4random.c;beginline=3;endline=20;md5=ced8636ecefa2ba907cfe390bc3bd964 \
                    file://lib/util/arc4random_uniform.c;beginline=3;endline=17;md5=e30c2b777cdc00cfcaf7c445a10b262f \
                    file://lib/util/getentropy.c;beginline=1;endline=19;md5=a0f58be3d60b6dcd898ec5fe0866d36f \
                    "

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"
PACKAGECONFIG[pam-wheel] = ",,,pam-plugin-wheel"
PACKAGECONFIG[audit] = "--with-linux-audit,--without-linux-audit,audit"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

EXTRA_OECONF = "--with-editor=${base_bindir}/vi --with-env-editor"

EXTRA_OECONF:append:libc-musl = " --disable-hardening "

do_compile:prepend () {
       # Remove build host references from config.h
       sed -i  \
           -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
           -e 's,--build=${BUILD_SYS},,g' \
           -e 's,--host=${HOST_SYS},,g' \
           ${B}/config.h
}

# Explicitly create ${localstatedir}/lib before do_install to ensure
# the directory is accessible by all users. Otherwise the mkinstalldirs
# script (from sudo) will recursively create ${localstatedir}/lib/sudo
# and then chmod each directory with 0700 permissions, which isn't what
# we want (i.e, users would not be able to access /var/lib).
do_install:prepend (){
	mkdir -p ${D}/${localstatedir}/lib
}

CVE_VERSION_SUFFIX = "patch"
