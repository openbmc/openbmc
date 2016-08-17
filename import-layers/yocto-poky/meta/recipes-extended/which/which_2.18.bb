SUMMARY = "Displays the full path of shell commands"
DESCRIPTION = "Which is a utility that prints out the full path of the \
executables that bash(1) would execute when the passed \
program names would have been entered on the shell prompt. \
It does this by using the exact same algorithm as bash."
SECTION = "libs"
HOMEPAGE = "http://carlo17.home.xs4all.nl/which/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PR = "r2"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/which/which-2.18.tar.gz/42d51938e48b91f6e19fabf216f5c3e9/which-${PV}.tar.gz \
           file://fix_name_conflict_group_member.patch \
           file://automake-foreign.patch \
"

SRC_URI[md5sum] = "42d51938e48b91f6e19fabf216f5c3e9"
SRC_URI[sha256sum] = "9445cd7e02ec0c26a44fd56098464ded064ba5d93dd2e15ec12410ba56b2e544"

DEPENDS = "cwautomacros-native"

inherit autotools texinfo update-alternatives

do_configure_prepend() {
	OLD="@ACLOCAL_CWFLAGS@"
	NEW="-I ${STAGING_DIR_NATIVE}/${datadir}/cwautomacros/m4"
	sed -i "s#${OLD}#${NEW}#g" `grep -rl ${OLD} ${S}`
}

ALTERNATIVE_${PN} = "which"
ALTERNATIVE_PRIORITY = "100"

