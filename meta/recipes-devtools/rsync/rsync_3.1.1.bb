require rsync.inc


SRC_URI += "file://acinclude.m4 \
            file://0001-Complain-if-an-inc-recursive-path-is-not-right-for-i.patch \
            file://rsync.git-eac858085.patch \
"

SRC_URI[md5sum] = "43bd6676f0b404326eee2d63be3cdcfe"
SRC_URI[sha256sum] = "7de4364fcf5fe42f3bdb514417f1c40d10bbca896abe7e7f2c581c6ea08a2621"

PACKAGECONFIG ??= "acl attr"
PACKAGECONFIG[acl] = "--enable-acl-support,--disable-acl-support,acl,"
PACKAGECONFIG[attr] = "--enable-xattr-support,--disable-xattr-support,attr,"

# rsync 3.0 uses configure.sh instead of configure, and
# makefile checks the existence of configure.sh
do_configure_prepend () {
	rm -f ${S}/configure ${S}/configure.sh
	cp -f ${WORKDIR}/acinclude.m4 ${S}/

	# By default, if crosscompiling, rsync disables a number of
	# capabilities, hardlinking symlinks and special files (i.e. devices)
	export rsync_cv_can_hardlink_special=yes
	export rsync_cv_can_hardlink_symlink=yes
}

do_configure_append () {
	cp -f ${S}/configure ${S}/configure.sh
}
